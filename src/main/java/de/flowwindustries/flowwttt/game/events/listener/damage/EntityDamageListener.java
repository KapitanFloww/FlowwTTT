package de.flowwindustries.flowwttt.game.events.listener.damage;

import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.game.events.EventSink;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Listen to a player death.
 */
@Log
@RequiredArgsConstructor
public class EntityDamageListener implements Listener {

    private final GameManagerService gameManagerService;
    private final EventSink eventSink;

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        log.info("Handling EntityDamageEvent: Entity: %s".formatted(event.getEntity().getName()));

        final var entity = event.getEntity();
        final var entityType = event.getEntityType();

        // Skip if victim is not player
        if (entityType != EntityType.PLAYER) {
            return;
        }

        // Skip if player is not in instance
        final var player = (Player) entity;
        if (!gameManagerService.isInCurrentInstance(player)) {
            log.info("Player is not in current instance. Skipping...");
            return;
        }
        final var instance = gameManagerService.getCurrentInstance();

        // Handle entity damage by entity if applicable
        if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            onEntityDamageByEntity(entityDamageByEntityEvent, instance, player);
            return;
        }

        // Simply handle entity damage event
        final TTTPlayerDamageEvent tttDamageEvent = new TTTPlayerDamageEvent()
                .withEventType(DamageEventType.ENTITY_DAMAGE)
                .withDamage(event.getDamage())
                .withInstance(instance)
                .withVictim(player)
                .withDamageCause(event.getCause())
                .withDamager(null)
                .withSourceEvent(event);

        eventSink.push(tttDamageEvent);
    }

    private void onEntityDamageByEntity(final EntityDamageByEntityEvent event, GameInstance instance, Player victim) {
        log.info("Handling EntityDamageByEntityEvent: Entity: %s, Damager: %s".formatted(event.getEntity().getName(), event.getDamager().getName()));

        final var damager = determineDamagerPlayer(event.getDamager());
        final TTTPlayerDamageEvent tttDamageEvent = new TTTPlayerDamageEvent()
                .withEventType(DamageEventType.ENTITY_DAMAGE_BY_ENTITY)
                .withDamage(event.getDamage())
                .withInstance(instance)
                .withVictim(victim)
                .withDamageCause(event.getCause())
                .withDamager(damager)
                .withSourceEvent(event);

        eventSink.push(tttDamageEvent);
    }

    private static Player determineDamagerPlayer(Entity entity) {
        final Player damager;
        switch (entity.getType()) {
            case ARROW -> damager = getShootingPlayerSafe((Arrow) entity);
            case PLAYER -> damager = (Player) entity;
            default -> throw new IllegalStateException("Damager [%s, %s] is neither PLAYER nor ARROW.".formatted(entity.getName(), entity.getType()));
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
        throw new IllegalArgumentException("Shooter [%s] is not a player.".formatted(shooter.toString()));
    }
}
