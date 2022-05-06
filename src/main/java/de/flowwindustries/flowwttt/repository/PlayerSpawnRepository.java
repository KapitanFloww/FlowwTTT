package de.flowwindustries.flowwttt.repository;

import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;

public class PlayerSpawnRepository extends AbstractRepository<PlayerSpawn, Integer> {
    public PlayerSpawnRepository(Class<PlayerSpawn> entityClass) {
        super(entityClass);
    }
}
