package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.locations.Arena;

/**
 * Service for spawn and de-spawn chests of an {@link Arena}.
 */
public interface ChestService {

    /**
     * Spawn chests of an {@link Arena}.
     * @param arena the arena
     */
    void spawnChests(Arena arena);

    /**
     * Despawn the chests of this {@link Arena}.
     * @param arena the arena
     */
    void deSpawnChests(Arena arena);
}
