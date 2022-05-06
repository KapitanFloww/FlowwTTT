package de.flowwindustries.flowwttt.repository;

import de.flowwindustries.flowwttt.domain.locations.Lobby;

public class LobbyRepository extends AbstractRepository<Lobby, String> {
    public LobbyRepository(Class<Lobby> entityClass) {
        super(entityClass);
    }
}
