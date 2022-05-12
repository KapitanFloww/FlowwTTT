package de.flowwindustries.flowwttt.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;

public class WorldParser {

    /**
     * Null-Safe way to get a world by its name.
     * @param name the name of the world
     * @return the parsed {@link World}
     * @throws IllegalArgumentException if the world does not exist
     */
    public static World getWorldSafe(String name) throws IllegalArgumentException {
        return Optional.ofNullable(Bukkit.getWorld(name)).orElseThrow(() -> new IllegalArgumentException("World does not exist: " + name));
    }
}
