package de.flowwindustries.flowwttt.game.events.listener.join;

import de.flowwindustries.flowwttt.services.GameManagerService;
import de.flowwindustries.flowwttt.utils.SpigotParser;
import lombok.extern.java.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

@Log
public class PlayerJoinListener implements Listener {

    private final GameManagerService gameManagerService;

    public PlayerJoinListener(GameManagerService gameManagerService) {
        this.gameManagerService = Objects.requireNonNull(gameManagerService);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        log.info("Handling PlayerJoinEvent: %s".formatted(event.getPlayer().getName()));
        final var instance = gameManagerService.getCurrentInstance();
        final var lobby = instance.getLobby();
        final var lobbyLocation = SpigotParser.mapSpawnToLocation(lobby.getLobbySpawn());
        event.getPlayer().teleport(lobbyLocation);
        gameManagerService.addPlayer(instance.getIdentifier(), event.getPlayer());
    }


}
