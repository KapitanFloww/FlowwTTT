package de.flowwindustries.flowwttt.utils;

import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Optional;

public class SpigotParser {

    /**
     * Null-Safe way to get a world by its name.
     * @param name the name of the world
     * @return the parsed {@link World}
     * @throws IllegalArgumentException if the world does not exist
     */
    public static World getWorldSafe(String name) throws IllegalArgumentException {
        return Optional.ofNullable(Bukkit.getWorld(name)).orElseThrow(() -> new IllegalArgumentException("World does not exist: " + name));
    }

    /**
    * Map {@link PlayerSpawn} to {@link Location}.
     * @param lobbySpawn the player spawn
     * @return the location
     */
    public static Location mapSpawnToLocation(PlayerSpawn lobbySpawn) {
        World world = getWorldSafe(lobbySpawn.getWorldName());
        Location location = new Location(world, lobbySpawn.getX(), lobbySpawn.getY(), lobbySpawn.getZ(), lobbySpawn.getYaw(), lobbySpawn.getPitch());
        return location;
    }
}
