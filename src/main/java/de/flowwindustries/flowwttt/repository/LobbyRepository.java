package de.flowwindustries.flowwttt.repository;

import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.locations.Lobby;

public class LobbyRepository extends AbstractRepository<Lobby, String> {
    public LobbyRepository(Class<Lobby> entityClass, FileConfigurationWrapper configurationWrapper) {
        super(entityClass, configurationWrapper);
    }
}
