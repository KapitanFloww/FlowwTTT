package de.flowwindustries.flowwttt.game.listener;

import org.bukkit.event.Event;

public interface EventSink {
    <E extends Event> void push(E event);
}
