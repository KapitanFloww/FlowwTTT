package de.flowwindustries.flowwttt.game.events.listener.death;

import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.game.events.listener.damage.TTTPlayerDamageEvent;
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

@With
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TTTPlayerReduceEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /**
     * The game's {@link StartInstanceEvent} the victim {@link Player} is currently in.
     * Never {@code null}.
     */
    private GameInstance instance;

    /**
     * The type of the reduction.
     */
    private ReductionType reductionType;

    /**
     * The victim {@link Player} that gets reduced.
     * Never {@code null}.
     */
    private Player victim;

    /**
     * The damaging {@link Player}.
     * This is {@code null}, if the reduction is not caused by a player or by natural causes.
     */
    private Player killer;

    /**
     * The source event that is responsible for calling this event.
     * Might be {@code null} if the reduce event is not called from another TTT-event.
     */
    private TTTPlayerDamageEvent tttSourceEvent;

    // Handlers
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
