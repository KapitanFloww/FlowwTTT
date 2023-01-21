package de.flowwindustries.flowwttt.game.events.listener.damage;

import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.game.events.listener.start.StartInstanceEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@With
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TTTPlayerDamageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /**
     * Type of the event.
     */
    private DamageEventType eventType;

    /**
     * The damage.
     */
    private Double damage;

    /**
     * The game's {@link StartInstanceEvent} the victim {@link Player} is currently in.
     * Never {@code null}.
     */
    private GameInstance instance;

    /**
     * The victim {@link Player} that has received the damage.
     * Never {@code null}.
     */
    private Player victim;

    /**
     * The cause of the damage.
     */
    private DamageCause damageCause;

    /**
     * The damaging {@link Player}.
     * This is {@code null}, if the damage is not caused by a mob or by natural causes.
     */
    private Player damager;

    /**
     * The source event. Might by {@link EntityDamageEvent} or its subclass {@link EntityDamageByEntityEvent}.
     */
    private EntityDamageEvent sourceEvent;

    // Handlers
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}