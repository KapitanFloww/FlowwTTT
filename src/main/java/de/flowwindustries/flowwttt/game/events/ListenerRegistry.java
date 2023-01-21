package de.flowwindustries.flowwttt.game.events;

import org.bukkit.event.Listener;

public interface ListenerRegistry {
    <T extends Listener> void registerListener(T listener);
}
