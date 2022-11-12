package de.flowwindustries.flowwttt.scheduled;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.GameInstance;
import de.flowwindustries.flowwttt.events.StartInstanceEvent;
import de.flowwindustries.flowwttt.services.ArenaService;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_MIN_PLAYERS;
import static de.flowwindustries.flowwttt.config.FileConfigurationWrapper.readInt;

@Log
public class Idler implements Runnable {

    private final int minRequiredPlayers;
    private final TTTPlugin plugin;
    private final GameInstance instance;
    private final ArenaService arenaService;

    public Idler(TTTPlugin plugin, GameInstance instance, ArenaService arenaService) {
        this.minRequiredPlayers = readInt(PATH_GAME_MIN_PLAYERS);
        this.plugin = plugin;
        this.instance = instance;
        this.arenaService = arenaService;
    }

    private Integer taskId;

    @Override
    public void run() {
        if(instance.getCurrentPlayersActive().size() >= minRequiredPlayers) {
            PlayerMessage.info("Game is starting...", instance.getCurrentPlayersActive());
            // Start the game
            StartInstanceEvent event = new StartInstanceEvent(instance.getIdentifier(), arenaService.getAll().stream().findAny().get()); // TODO get voted arena instead
            Bukkit.getServer().getPluginManager().callEvent(event);
            if(taskId != null) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
            return;
        }
        log.info(String.format("Not enough players for instance %s (%s/%s)", instance.getIdentifier(), instance.getCurrentPlayersActive().size(), minRequiredPlayers));
        PlayerMessage.info(String.format("Not enough players (%s/%s). Waiting for players...", instance.getCurrentPlayersActive().size(), minRequiredPlayers), instance.getCurrentPlayersActive());
    }

    public void scheduleIdler() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 200L, 200L); // 200 ticks = 10 seconds
    }
}
