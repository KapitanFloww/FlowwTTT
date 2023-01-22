package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.domain.items.ChestType;
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
        arena.getChestSpawns().forEach(chestSpawn -> {
            final var chestBlock = CHEST_LOCATION_FUNCTION.apply(chestSpawn).getBlock();
            final var chestMaterial = getChestMaterial(chestSpawn.getType());

            if(chestBlock.getType() != Material.AIR) {
                log.warning("Location of chest %s obstructed: %s, %s, %s".formatted(chestSpawn.getId(), chestBlock.getX(), chestBlock.getY(), chestBlock.getZ()));
                return;
            }
            chestBlock.setType(chestMaterial);
            log.info("Spawned chest %s (%s) in arena %s".formatted(chestSpawn.getId(), chestSpawn.getType(), arena.getArenaName()));
        });
    }

    @Override
    public void despawnChests(Arena arena) {
        arena.getChestSpawns().stream()
                .map(CHEST_LOCATION_FUNCTION)
                .map(Location::getBlock)
                .forEach(block -> {
                    if(block.getType() == Material.CHEST || block.getType() == Material.ENDER_CHEST) {
                        block.setType(Material.AIR);
                        return;
                    }
                    log.warning(String.format("Found no chest on: %s, %s, %s", block.getX(), block.getY(), block.getZ()));
                });
        log.info(String.format("Despawned chests in arena %s", arena.getArenaName()));
    }

    private static final Function<ChestSpawn, Location> CHEST_LOCATION_FUNCTION = chestSpawn -> {
        World world = getWorldSafe(chestSpawn.getWorldName());
        return new Location(world, chestSpawn.getX(), (chestSpawn.getY()), chestSpawn.getZ());
    };

    private static Material getChestMaterial(ChestType chestType) {
        return chestType == ChestType.DEFAULT ? Material.CHEST : Material.ENDER_CHEST;
    }
}
