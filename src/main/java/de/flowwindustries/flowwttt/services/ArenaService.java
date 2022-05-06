package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.ChestSpawn;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;

import java.util.Collection;

/**
 * Service class for managing {@link Arena}s.
 */
public interface ArenaService {

    /**
     * Create a new arena with the given name.
     * @param name the name of the arena
     */
    void createArena(String name);

    /**
     * Add a chest spawn point to the arena.
     * @param name he name of the arena
     * @param chestSpawn the location of the chest spawner
     * @return the id of the created spawn
     * @throws IllegalArgumentException if the arena does not exist
     */
    void addChestSpawn(String name, ChestSpawn chestSpawn) throws IllegalArgumentException;

    /**
     * Add a player spawn point to the arena.
     * @param name the name of the arena
     * @param playerSpawn the location of the player spawn point
     * @return the id of the created spawn
     * @throws IllegalArgumentException if the arena does not exist
     */
    void addPlayerSpawn(String name, PlayerSpawn playerSpawn) throws IllegalArgumentException;

    /**
     * Get an arena by its name.
     * @param name the name of the arena
     * @return the arena
     * @throws IllegalArgumentException if the arena does not exist
     */
    Arena getArenaSafe(String name) throws IllegalArgumentException;

    /**
     * Get all arenas.
     * @return a collection of all arenas
     */
    Collection<Arena> getAll();

    /**
     * Update the name of an arena.
     * @param oldName the old name of the arena
     * @param newName the new name of the arena
     * @return the updated arena
     */
    Arena updateName(String oldName, String newName);

    /**
     * Clear a specific player spawn point of the arena.
     * @param name the name of the arena
     * @param id the id of the player spawn point to delete
     * @return the updated arena
     * @throws IllegalArgumentException if the arena or spawn point does not exist
     */
    Arena clearPlayerSpawn(String name, int id) throws IllegalArgumentException;

    /**
     * Clear a specific chest spawn point of the arena.
     * @param name the name of the arena
     * @param id the id of the chest spawn point to delete
     * @return the updated arena
     * @throws IllegalArgumentException if the arena or spawn point does not exist
     */
    Arena clearChestSpawn(String name, int id) throws IllegalArgumentException;

    /**
     * Delete the arena with the given name.
     * @param name the name of the arena to delete
     */
    void deleteArena(String name);
}
