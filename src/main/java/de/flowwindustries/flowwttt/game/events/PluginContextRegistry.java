package de.flowwindustries.flowwttt.game.events;

import lombok.extern.java.Log;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;

@Log
public class PluginContextRegistry implements ListenerRegistry {

    private final PluginManager pluginManager;
    private final Plugin plugin;

    public PluginContextRegistry(PluginManager pluginManager, Plugin plugin) {
        this.pluginManager = Objects.requireNonNull(pluginManager);
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public <T extends Listener> void registerListener(T listener) {
        log.info("Register TTT listener %s".formatted(listener));
        pluginManager.registerEvents(listener, plugin);
    }
}
