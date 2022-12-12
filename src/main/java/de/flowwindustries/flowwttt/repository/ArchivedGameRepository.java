package de.flowwindustries.flowwttt.repository;

import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.ArchivedGame;

/**
 * Repository for {@link ArchivedGame}.
 */
public class ArchivedGameRepository extends AbstractRepository<ArchivedGame, String> {

    public ArchivedGameRepository(Class<ArchivedGame> entityClass, FileConfigurationWrapper configurationWrapper) {
        super(entityClass, configurationWrapper);
    }
}
