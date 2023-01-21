package de.flowwindustries.flowwttt.game.events.listener.death;

import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.game.events.EventSink;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@Log
@RequiredArgsConstructor
public class PlayerQuitListener implements Listener {

    private final EventSink eventSink;
    private final GameManagerService gameManagerService;

    @EventHandler
    public void onPlayerDamage(final PlayerQuitEvent event) {
        log.info("Handling PlayerQuitEvent: Player %s".formatted(event.getPlayer()));
        final GameInstance instance = gameManagerService.getInstanceOf(event.getPlayer());

        // Skip if player is not in valid instance
        if(instance == null) {
            return;
        }

        TTTPlayerReduceEvent reduceEvent = new TTTPlayerReduceEvent()
                .withInstance(instance)
                .withReductionType(ReductionType.QUIT)
                .withVictim(event.getPlayer())
                .withKiller(null)
                .withTttSourceEvent(null);
        eventSink.push(reduceEvent);
    }
}
