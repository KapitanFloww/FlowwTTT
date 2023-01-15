package de.flowwindustries.flowwttt.game.listener;

import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.events.PlayerReduceEvent;
import de.flowwindustries.flowwttt.events.ReductionType;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Listen to a player death.
 */
@Log
@RequiredArgsConstructor
public class PlayerDamageListener implements Listener {

    private final GameManagerService gameManagerService;
    private final EventSink eventSink;

    /**
     * Block damage to players when they are in a valid game instance and the stage is not {@link Stage#RUNNING}.
     * Handles "death" of a player when being killed by another player.
     * @param event - the event to handle
     */
    @EventHandler
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {

        // Check if damaged entity is player
        if (!(event.getEntity() instanceof Player player)) {
            log.fine("Damaged entity a not player. Skipping event handling...");
            return;
        }
        // Check if player is in valid game instance
        GameInstance instance = gameManagerService.getInstanceOf(player);
        if (instance == null) {
            log.fine("Player %s is not in a valid game instance".formatted(player.getName()));
            return;
        }

        // When stage is NOT in running, disable the damage
        if (instance.getCurrentStage().getName() != Stage.RUNNING) {
            log.fine("Instance not in %s-stage".formatted(Stage.RUNNING));
            disableDamage(event);
            return;
        }

        // Else handle the damage
        final Player damager = determineDamager(event.getDamager());
        handleDamage(event, instance, player, damager);
    }

    private static void disableDamage(final EntityDamageByEntityEvent event) {
        log.fine("Disabling damage...");

        // Disable default damage
        event.setDamage(0.0d);

        // Check for arrow
        final Entity damageEntity = event.getDamager();
        if (damageEntity instanceof Arrow arrow) {
            arrow.setDamage(0.0d);
        }
        event.setCancelled(true);
    }

    private static Player determineDamager(Entity entity) {
        final Player damager;
        switch (entity.getType()) {
            case ARROW -> damager = getShootingPlayerSafe((Arrow) entity);
            case PLAYER -> damager = (Player) entity;
            default -> throw new RuntimeException("Damager is %s neither player nor arrow.".formatted(entity.getName()));
        }
        return damager;
    }

    private static Player getShootingPlayerSafe(Arrow arrow) throws IllegalArgumentException {
        final var shooter = arrow.getShooter();
        if (shooter == null) {
            throw new IllegalArgumentException("Shooter must not be null");
        }
        if (shooter instanceof Player player) {
            return player;
        }
        throw new IllegalArgumentException("Shooter is not a player: %s".formatted(shooter.toString()));
    }

    // Check for "death" of a player
    private void handleDamage(final EntityDamageByEntityEvent event, final GameInstance instance, final Player victim, final Player damager) {
        if((victim.getHealth() - event.getDamage()) <= 0) {
            log.info("Handling player kill: Victim: %s Killer: %s".formatted(victim.getName(), damager.getName()));

            // Disable the event
            event.setDamage(0.0d);
            event.setCancelled(true);

            handleVictim(victim, damager, instance);
            handleKiller(victim, damager, instance);

            PlayerReduceEvent reduceEvent = new PlayerReduceEvent(instance, ReductionType.DEATH, victim);
            eventSink.push(reduceEvent);
        }
    }

    private void handleVictim(Player victim, Player killer, GameInstance instance) {
        instance.notifyPlayer(victim, "You have been murdered by %s".formatted(killer.getName()));
        instance.heal(victim);
        instance.setGameMode(victim, GameMode.SPECTATOR);
    }

    private void handleKiller(Player victim, Player killer, GameInstance instance) {
        var role = instance.getPlayerRoles().get(victim);
        instance.notifyPlayer(killer, "You killed %s [%s]".formatted(victim.getName(), role));
        // TODO KapitanFloww add or remove karma
    }
}
