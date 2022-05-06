package de.flowwindustries.flowwttt.repository;

import de.flowwindustries.flowwttt.domain.locations.ChestSpawn;

public class ChestSpawnRepository extends AbstractRepository<ChestSpawn, Integer> {
    public ChestSpawnRepository(Class<ChestSpawn> entityClass) {
        super(entityClass);
    }
}
