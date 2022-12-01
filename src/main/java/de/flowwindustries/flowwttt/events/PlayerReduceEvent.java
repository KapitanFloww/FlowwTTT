package de.flowwindustries.flowwttt.events;

import de.flowwindustries.flowwttt.game.GameInstance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class PlayerReduceEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final GameInstance instance;
    private final ReductionType reductionType;
    private final Player victim;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
