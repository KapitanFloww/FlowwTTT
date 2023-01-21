package de.flowwindustries.flowwttt.game.events;

import org.bukkit.event.Event;

public interface EventSink {
    <E extends Event> void push(E event);
}
