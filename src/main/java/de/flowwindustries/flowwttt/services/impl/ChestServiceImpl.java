package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.ChestSpawn;
import de.flowwindustries.flowwttt.services.ChestService;
import lombok.extern.java.Log;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.function.Function;

import static de.flowwindustries.flowwttt.utils.SpigotParser.getWorldSafe;

@Log
public class ChestServiceImpl implements ChestService {

    @Override
    public void spawnChests(Arena arena) {
        arena.getChestSpawns().stream()
                .map(chestToLocation)
                .map(Location::getBlock)
                .forEach(block -> {
                    if(block.getType() == Material.AIR) {
                        block.setType(Material.CHEST);
                        return;
                    }
                    log.warning(String.format("Chest location obstructed: %s, %s, %s", block.getX(), block.getY(), block.getZ()));
                });
        log.info(String.format("Spawned %s chests in arena %s", arena.getChestSpawns().size(), arena.getArenaName()));
    }

    @Override
    public void deSpawnChests(Arena arena) {
        arena.getChestSpawns().stream()
                .map(chestToLocation)
                .map(Location::getBlock)
                .forEach(block -> {
                    if(block.getType() == Material.CHEST) {
                        block.setType(Material.AIR);
                        return;
                    }
                    log.warning(String.format("Found no chest on: %s, %s, %s", block.getX(), block.getY(), block.getZ()));
                });
        log.info(String.format("De-spawned chests in arena %s", arena.getArenaName()));
    }

    private final Function<ChestSpawn, Location> chestToLocation = chestSpawn -> {
        World world = getWorldSafe(chestSpawn.getWorldName());
        return new Location(world, chestSpawn.getX(), (chestSpawn.getY()), chestSpawn.getZ());
    };
}
