package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.locations.Lobby;

import java.util.Collection;

public interface LobbyService {

    Lobby createNewLobby();

    Lobby getLobbySafe(String identifier);

    Collection<Lobby> getAll(String worldName);

    Lobby updateLobby(String identifier);

    void deleteLobby();
}
