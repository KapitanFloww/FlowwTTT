package de.flowwindustries.flowwttt.repository;

import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.locations.Arena;

/**
 * Arena repository.
 */
public class ArenaRepository extends AbstractRepository<Arena, String> {

    public ArenaRepository(Class<Arena> entityClass, FileConfigurationWrapper configurationWrapper) {
        super(entityClass, configurationWrapper);
    }
}
