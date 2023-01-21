package de.flowwindustries.flowwttt.game.events.listener.start;

import de.flowwindustries.flowwttt.domain.locations.Arena;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class StartInstanceEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final String instanceId;
    private final Arena arena;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public Arena getArena() {
        return arena;
    }
}
