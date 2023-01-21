package de.flowwindustries.flowwttt.game.events.listener.start;

import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Log
@RequiredArgsConstructor
public class StartInstanceListener implements Listener {

    private final GameManagerService gameManagerService;

    @EventHandler
    public void handleStartInstanceEvent(StartInstanceEvent event) {
        log.info(String.format("Received event to start instance %s with arena %s", event.getInstanceId(), event.getArena().getArenaName()));
        gameManagerService.start(event.getInstanceId(), event.getArena());
    }
}
