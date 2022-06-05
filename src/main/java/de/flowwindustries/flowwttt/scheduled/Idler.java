package de.flowwindustries.flowwttt.scheduled;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.config.ConfigurationUtils;
import de.flowwindustries.flowwttt.domain.GameInstance;
import de.flowwindustries.flowwttt.events.StartInstanceEvent;
import de.flowwindustries.flowwttt.services.ArenaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_MIN_PLAYERS;

@Log
@RequiredArgsConstructor
public class Idler implements Runnable {

    private static final int MIN_REQUIRED_PLAYERS = ConfigurationUtils.read(Integer.class, PATH_GAME_MIN_PLAYERS);

    private Integer taskId;
    private final TTTPlugin plugin;
    private final GameInstance instance;
    private final ArenaService arenaService;

    @Override
    public void run() {
        if(instance.getCurrentPlayers().size() >= MIN_REQUIRED_PLAYERS) {
            PlayerMessage.info("Game is starting...", instance.getCurrentPlayers());
            // Start the game
            StartInstanceEvent event = new StartInstanceEvent(instance.getIdentifier(), arenaService.getAll().stream().findAny().get()); // TODO get voted arena instead
            Bukkit.getServer().getPluginManager().callEvent(event);
            if(taskId != null) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
            return;
        }
        log.info(String.format("Not enough players for instance %s (%s/%s)", this.instance.getIdentifier(), this.instance.getCurrentPlayers().size(), MIN_REQUIRED_PLAYERS));
        PlayerMessage.info(String.format("Not enough players (%s/%s). Waiting for players...", this.instance.getCurrentPlayers().size(), MIN_REQUIRED_PLAYERS), this.instance.getCurrentPlayers());
    }

    public void scheduleIdler() {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 200L, 200L); // 200 ticks = 10 seconds
    }
}
