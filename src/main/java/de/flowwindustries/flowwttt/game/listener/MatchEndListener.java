package de.flowwindustries.flowwttt.game.listener;

import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.events.PlayerReduceEvent;
import de.flowwindustries.flowwttt.events.ReductionType;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener to listen for player deaths or quits.
 * Recalculates if the match should end then.
 */
@Log
@RequiredArgsConstructor
public class MatchEndListener implements Listener {

    private final GameManagerService gameManagerService;
    private final EventSink eventSink;

    @EventHandler
    public void onPlayerDamage(final PlayerQuitEvent event) {
        GameInstance instance = gameManagerService.getInstanceOf(event.getPlayer());
        if(instance != null) {
            PlayerReduceEvent reduceEvent = new PlayerReduceEvent(instance, ReductionType.QUIT, event.getPlayer());
            eventSink.push(reduceEvent);
        }
    }

    @EventHandler
    public void onPlayerReduce(final PlayerReduceEvent event) {
        // TODO KapitanFloww what to do when match is in lobby, countdown or grace-period
        // Check if instance is still running
        log.info("Handling player reduction for instance %s. Type: %s. victim: %s".formatted(event.getInstance().getIdentifier(), event.getReductionType(), event.getVictim().getName()));
        if(event.getInstance().getCurrentStage().getName() != Stage.RUNNING) {
            return;
        }

        // Remove player from instance
        var instance = event.getInstance();
        instance.removePlayer(event.getVictim(), event.getReductionType());

        recalculateGameResult(instance);
    }

    private static void recalculateGameResult(GameInstance instance) {
        log.info("Recalculating game stats for instance %s".formatted(instance.getIdentifier()));
        if (instance.getGameResult() != GameResult.PENDING) {
            throw new IllegalStateException("GameResult is already set to: %s".formatted(instance.getGameResult()));
        }

        // Check for traitor win
        var traitorsCount = getRoleCount(instance, Role.TRAITOR);
        if(traitorsCount == 0) {
            innocentsWin(instance);
            return;
        }

        // Check for inno win
        var innocentsCount = getRoleCount(instance, Role.INNOCENT);
        var detectiveCount = getRoleCount(instance, Role.DETECTIVE);
        if(innocentsCount + detectiveCount == 0) {
            traitorsWin(instance);
        }
    }

    private static int getRoleCount(GameInstance gameInstance, Role role) {
        return gameInstance.getCurrentPlayersActive().stream()
                .filter(player -> gameInstance.getPlayerRoles().get(player) == role)
                .toList()
                .size();
    }

    private static void innocentsWin(GameInstance gameInstance) {
        gameInstance.setGameResult(GameResult.INNOCENT_WIN);
        gameInstance.startNext();
    }

    private static void traitorsWin(GameInstance gameInstance) {
        gameInstance.setGameResult(GameResult.TRAITOR_WIN);
        gameInstance.startNext();
    }
}
