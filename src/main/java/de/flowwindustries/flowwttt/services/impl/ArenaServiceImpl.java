package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.ChestSpawn;
import de.flowwindustries.flowwttt.domain.Identifiable;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import de.flowwindustries.flowwttt.repository.ArenaRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Service implementation of {@link ArenaService}.
 */
@Log
@RequiredArgsConstructor
public class ArenaServiceImpl implements ArenaService {

    private static final String ARENA_NOT_FOUND = "Arena: %s does not exist";
    private static final String ARENA_SPAWN_NOT_FOUND = "Spawn point with id: %s not found";

    private final ArenaRepository arenaRepository;

    @Override
    public void createArena(String name) {
        Arena arena = new Arena();
        arena.setArenaName(name);
        arena.setPlayerSpawns(new ArrayList<>());
        arena.setChestSpawns(new ArrayList<>());
        arenaRepository.create(arena);
        log.info("Created new arena: " + arena.getArenaName());
    }

    @Override
    public void addChestSpawn(String name, ChestSpawn chestSpawn) throws IllegalArgumentException {
        Arena arena = getArenaSafe(name);
        log.info("Adding chest spawn:" + chestSpawn + " to arena: " + name);

        Collection<ChestSpawn> chestSpawns = arena.getChestSpawns();
        chestSpawns.add(chestSpawn);
        arena.setChestSpawns(chestSpawns);
        arenaRepository.edit(arena);
    }

    @Override
    public void addPlayerSpawn(String name, PlayerSpawn playerSpawn) throws IllegalArgumentException {
        Arena arena = getArenaSafe(name);
        log.info("Adding player spawn:" + playerSpawn + " to arena: " + name);

        Collection<PlayerSpawn> playerSpawns = arena.getPlayerSpawns();
        playerSpawns.add(playerSpawn);
        arena.setPlayerSpawns(playerSpawns);
        arenaRepository.edit(arena);
    }

    @Override
    public Arena getArenaSafe(String name) throws IllegalArgumentException {
        log.info("Request to get arena: " + name);
        return arenaRepository.find(name)
                .orElseThrow(() -> new IllegalArgumentException(String.format(ARENA_NOT_FOUND, name)));
    }

    @Override
    public Collection<Arena> getAll() {
        log.info("Request to get all arenas");
        return arenaRepository.findAll();
    }

    @Override
    public Arena updateName(String oldName, String newName) {
        Arena arena = getArenaSafe(oldName);
        log.info("Request to update arena name of: " + oldName + " to: " + newName);
        arena.setArenaName(newName);
        return arenaRepository.edit(arena);
    }

    @Override
    public void clearPlayerSpawn(String name, int id) throws IllegalArgumentException {
        Arena arena = getArenaSafe(name);
        log.info("Request to remove player spawn with id: " + id + " from arena: " + name);

        PlayerSpawn spawn = filterSpawnSafe(arena.getPlayerSpawns(), id);
        arena.getPlayerSpawns().remove(spawn);
        arenaRepository.edit(arena);
    }

    @Override
    public void clearChestSpawn(String name, int id) throws IllegalArgumentException {
        Arena arena = getArenaSafe(name);
        log.info("Request to remove chest spawn with id: " + id + " from arena: " + name);

        ChestSpawn spawn = filterSpawnSafe(arena.getChestSpawns(), id);
        arena.getChestSpawns().remove(spawn);
        arenaRepository.edit(arena);
    }

    @Override
    public void deleteArena(String name) {
        log.info("Request to delete arena: " + name);
        try {
            Arena arena = getArenaSafe(name);
            arenaRepository.remove(arena);
        } catch (IllegalArgumentException ignored) {}
    }

    private <S extends Identifiable<?>> S filterSpawnSafe(Collection<S> spawns, int id) {
        Optional<S> spawn =  spawns.stream()
                .filter(playerSpawn -> playerSpawn.getId().equals(id))
                .findFirst();
        if(spawn.isEmpty()) {
            throw new IllegalArgumentException(String.format(ARENA_SPAWN_NOT_FOUND, id));
        }
        return spawn.get();
    }
}
