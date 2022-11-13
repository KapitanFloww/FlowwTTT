package de.flowwindustries.flowwttt.scheduled;

import de.flowwindustries.flowwttt.TTTPlugin;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;

import java.util.function.Consumer;

/**
 * Class to simplify the implementation of countdowns.
 * Simply create a new instance of this class and call {@code scheduleCountdown()}.
 * Based on <a href="https://www.spigotmc.org/threads/creating-a-countdown.266702/">ExpDev from Spigotmc.org</a>
 */
@Log
public class Countdown implements Runnable {

    /**
     * This task's id - required for canceling.
     */
    private Integer taskId;

    /**
     * Plugin instance.
     */
    private final TTTPlugin plugin;

    /**
     * Associated instance id.
     */
    private final String instanceId;

    /**
     * Task to be run before the countdown starts.
     */
    private final Runnable beforeCountdown;

    /**
     * Task to be run when the countdown ends.
     */
    private final Runnable afterCountdown;

    /**
     * To do each second.
     */
    private final Consumer<Countdown> secondsConsumer;

    /**
     * The total time.
     */
    @Getter
    private final int time;

    /**
     * The current time left.
     */
    @Getter
    private int timeLeft;

    public Countdown(TTTPlugin plugin, String instanceId, int totalSeconds, Runnable beforeCountdown, Runnable afterCountdown, Consumer<Countdown> secondsConsumer) {
        this.plugin = plugin;
        this.instanceId = instanceId;
        this.time = totalSeconds;
        this.timeLeft = totalSeconds;
        this.beforeCountdown = beforeCountdown;
        this.afterCountdown = afterCountdown;
        this.secondsConsumer = secondsConsumer;
    }

    /**
     * Countdown logic to be performed.
     */
    @Override
    public void run() {
        if(timeLeft < 1) {
            afterCountdown.run();
            cancel();
        }
        if(time == timeLeft) { // Start
            beforeCountdown.run();
        }
        secondsConsumer.accept(this);
        timeLeft--;
    }

    /**
     * Schedule and run this countdown.
     */
    public void scheduleCountdown() {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L); // Run every 20 ticks = 1 second
    }

    /**
     * Cancel this countdown.
     */
    public void cancel() {
        if(taskId != null) {
            log.info("Canceling task %s".formatted(taskId));
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}
