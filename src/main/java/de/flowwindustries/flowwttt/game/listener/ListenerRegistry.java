package de.flowwindustries.flowwttt.game.listener;

import org.bukkit.event.Listener;

public interface ListenerRegistry {
    <T extends Listener> void registerListener(T listener);
}
