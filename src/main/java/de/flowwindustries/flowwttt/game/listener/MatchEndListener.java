package de.flowwindustries.flowwttt.game.listener;

import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static de.flowwindustries.flowwttt.domain.enumeration.GameResult.PENDING;
import static de.flowwindustries.flowwttt.domain.enumeration.GameResult.TRAITOR_WIN;

/**
 * Listener to listen for player deaths or quits.
 * Recalculates if the match should end then.
 */
@Log
@RequiredArgsConstructor
public class MatchEndListener implements Listener {

    private final GameManagerService gameManagerService;

    @EventHandler
    public void onPlayerDamage(PlayerQuitEvent event) {
        GameInstance gameInstance = gameManagerService.getInstanceOf(event.getPlayer());
        handleEvent(gameInstance);
    }

    @EventHandler
    public void onPlayerDamage(PlayerDeathEvent event) {
        GameInstance gameInstance = gameManagerService.getInstanceOf(event.getEntity());
        handleEvent(gameInstance);
    }

    private static void handleEvent(GameInstance gameInstance) {
        // Check if player is in instance
        if(gameInstance == null) {
            return;
        }
        // Check if instance is running
        // TODO KapitanFloww what to do when match is in lobby, countdown or grace-period
        if(gameInstance.getCurrentStage().getName() != Stage.RUNNING) {
            return;
        }
        recalculateGameResult(gameInstance);
    }

    private static void recalculateGameResult(GameInstance gameInstance) {
        if (gameInstance.getGameResult() != PENDING) {
            throw new IllegalStateException("GameResult is already set to: %s".formatted(gameInstance.getGameResult()));
        }

        // Check for traitor win
        var traitorsCount = getRoleCount(gameInstance, Role.TRAITOR);
        if(traitorsCount == 0) {
            innocentsWin(gameInstance);
            return;
        }

        // Check for inno win
        var innocentsCount = getRoleCount(gameInstance, Role.INNOCENT);
        var detectiveCount = getRoleCount(gameInstance, Role.DETECTIVE);
        if(innocentsCount + detectiveCount == 0) {
            traitorsWin(gameInstance);
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
        gameInstance.setGameResult(TRAITOR_WIN);
        gameInstance.startNext();
    }
}
