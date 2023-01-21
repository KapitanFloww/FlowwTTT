package de.flowwindustries.flowwttt.game.events.listener.damage;

import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.events.listener.death.ReductionType;
import de.flowwindustries.flowwttt.game.events.listener.death.TTTPlayerReduceEvent;
import de.flowwindustries.flowwttt.game.events.EventSink;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

@Log
@RequiredArgsConstructor
public class TTTPlayerDamageEventListener implements Listener {

    private final EventSink eventSink;

    @EventHandler
    public void handleTTTPlayerDamage(final TTTPlayerDamageEvent event) {
        log.info("Handling TTTPlayerDamageEvent: Victim: %s, Damager: %s, DamageCause: %s".formatted(event.getVictim().getName(), event.getDamager().getName(), event.getDamageCause()));

        final var instance = event.getInstance();
        final var victim = event.getVictim();
        final var damage = event.getDamage();

        // Check if damage should be disabled
        if (instance.getCurrentStage().getName() != Stage.RUNNING) {
            disableDamage(event);
            return;
        }

        // Return if victim would not die. Else throw player reduce event.
        if ((victim.getHealth() - damage) > 0) {
            return;
        }
        final var reductionType = event.getDamager() != null ? ReductionType.KILL : ReductionType.DEATH;
        final TTTPlayerReduceEvent reduceEvent = new TTTPlayerReduceEvent()
                .withInstance(instance)
                .withReductionType(reductionType)
                .withVictim(victim)
                .withKiller(event.getDamager())
                .withTttSourceEvent(event);

        eventSink.push(reduceEvent);
    }


    // Disable damage sources and cancel the damage events
    private void disableDamage(final TTTPlayerDamageEvent event) {
        log.info("Disabling damage for event: %s".formatted(event));
        switch (event.getEventType()) {

            case ENTITY_DAMAGE -> {
                final var damageEvent = event.getSourceEvent();
                cancelEvent(damageEvent);
            }

            case ENTITY_DAMAGE_BY_ENTITY -> {
                final var damageEvent = (EntityDamageByEntityEvent) event.getSourceEvent();
                // Disable arrow damage
                if (event.getDamageCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                    final var arrow = (Arrow) damageEvent.getDamager();
                    arrow.setDamage(0.0d);
                }
                cancelEvent(damageEvent);
            }

            default -> throw new IllegalStateException("Invalid DamageEventType: %s".formatted(event.getEventType()));
        }
    }

    private void cancelEvent(final EntityDamageEvent event) {
        event.setDamage(0.0d);
        event.setCancelled(true);
    }
}
