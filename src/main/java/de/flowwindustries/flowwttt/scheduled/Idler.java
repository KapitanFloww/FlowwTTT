package de.flowwindustries.flowwttt.scheduled;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.events.StartInstanceEvent;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.game.listener.EventSink;
import de.flowwindustries.flowwttt.services.ArenaService;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;

import java.util.Objects;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_MIN_PLAYERS;

@Log
public class Idler implements Runnable {

    private final int minRequiredPlayers;
    private final TTTPlugin plugin;
    private final GameInstance instance;
    private final ArenaService arenaService;
    private final EventSink eventSink;

    public Idler(TTTPlugin plugin, GameInstance instance, ArenaService arenaService, EventSink eventSink, FileConfigurationWrapper fileConfigurationWrapper) {
        this.minRequiredPlayers = Objects.requireNonNull(fileConfigurationWrapper).readInt(PATH_GAME_MIN_PLAYERS);
        this.plugin = plugin;
        this.instance = instance;
        this.arenaService = arenaService;
        this.eventSink = eventSink;
    }

    private Integer taskId;

    @Override
    public void run() {
        if(instance.getCurrentPlayersActive().size() >= minRequiredPlayers) {
            PlayerMessage.info("Game is starting...", instance.getCurrentPlayersActive());
            // Start the game - for now get a random arena
            StartInstanceEvent startInstanceEvent = new StartInstanceEvent(instance.getIdentifier(), arenaService.getAll().stream().findAny().get()); // TODO KapitanFloww get specific/voted arena instead
            eventSink.push(startInstanceEvent);
            cancel();
            return;
        }
        log.info(String.format("Not enough players for instance %s (%s/%s)", instance.getIdentifier(), instance.getCurrentPlayersActive().size(), minRequiredPlayers));
        PlayerMessage.info(String.format("Not enough players (%s/%s). Waiting for players...", instance.getCurrentPlayersActive().size(), minRequiredPlayers), instance.getCurrentPlayersActive());
    }

    public void schedule() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 200L, 200L); // 200 ticks = 10 seconds
    }

    public void cancel() {
        if(taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
            return;
        }
        throw new IllegalStateException("TaskId must not be null");
    }
}