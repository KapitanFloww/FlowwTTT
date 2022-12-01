package de.flowwindustries.flowwttt.game.listener;

import lombok.extern.java.Log;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;

@Log
public class PluginContextEventSink implements EventSink {

    private final PluginManager pluginManager;

    public PluginContextEventSink(PluginManager pluginManager) {
        this.pluginManager = Objects.requireNonNull(pluginManager);
    }

    @Override
    public <E extends Event> void push(E event) {
        log.info("Calling event: %s".formatted(event));
        pluginManager.callEvent(event);
    }
}
