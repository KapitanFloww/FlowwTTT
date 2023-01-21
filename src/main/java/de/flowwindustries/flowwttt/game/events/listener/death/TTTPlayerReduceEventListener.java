package de.flowwindustries.flowwttt.game.events.listener.death;

import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.GameInstance;
import lombok.extern.java.Log;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

@Log
public class TTTPlayerReduceEventListener implements Listener {

    @EventHandler
    public void handleTTTPlayerReduceEvent(final TTTPlayerReduceEvent event) {
        log.info("Handling TTTPlayerReduceEvent: Victim %s, Killer: %s".formatted(event.getVictim(), event.getKiller()));

        final var instance = event.getInstance();

        switch (event.getReductionType()) {
            case QUIT -> {
                // Update instance
                instance.removePlayer(event.getVictim(), event.getReductionType());
            }
            case REMOVAL -> {

                // Handle victim
                instance.notifyPlayer(event.getVictim(), "You have been removed from this game");
                // Update instance
                instance.removePlayer(event.getVictim(), event.getReductionType());
            }
            case DEATH -> {

                // Handle victim
                handleVictim(event.getVictim(), null, instance, event.getTttSourceEvent().getDamageCause());
                // Update instance
                instance.killPlayer(event.getVictim(), event.getReductionType());
            }
            case KILL -> {

                // Handle victim and killer
                handleVictim(event.getVictim(), event.getKiller(), instance, event.getTttSourceEvent().getDamageCause());
                handleKiller(event.getVictim(), event.getKiller(), instance);
                // Update instance
                instance.killPlayer(event.getVictim(), event.getReductionType());
            }
            default -> throw new IllegalStateException("Invalid ReductionType: %s".formatted(event.getReductionType()));
        }

        // If instance is still RUNNING, recalculate game result
        if (instance.getCurrentStage().getName() == Stage.RUNNING) {
            recalculateGameResult(instance);
        }
    }

    private static void handleVictim(Player victim, Player killer, GameInstance instance, EntityDamageEvent.DamageCause damageCause) {
        if (killer != null) {
            instance.notifyPlayer(victim, "You have been murdered by %s".formatted(killer.getName()));
        } else {
            instance.notifyPlayer(victim, "You died from damage by %s".formatted(damageCause.name()));
        }
        instance.heal(victim);
        instance.setGameMode(victim, GameMode.SPECTATOR);
    }

    private static void handleKiller(Player victim, Player killer, GameInstance instance) {

        String message = "";
        var victimRole = instance.getPlayerRoles().get(victim);
        var killerRole = instance.getPlayerRoles().get(killer);

        if (killerRole == Role.TRAITOR) {
            switch (victimRole) {
                case INNOCENT, DETECTIVE -> message = "You've killed %s (%s)";
                case TRAITOR -> message = "You've butchered your own kind by killing %s (%s)";
            }
        } else {
            switch (victimRole) {
                case INNOCENT, DETECTIVE -> message = "You've killed the innocent %s (%s)";
                case TRAITOR -> message = "You saw through the lies and killed traitor %s (%s)";
            }
        }
        instance.notifyPlayer(killer, message.formatted(victim.getName(), killerRole));
        // TODO KapitanFloww add or remove karma
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
