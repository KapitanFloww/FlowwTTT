package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;

import java.util.Collection;

/**
 * Service class for managing {@link de.flowwindustries.flowwttt.domain.locations.Lobby}s.
 */
public interface LobbyService {

    /**
     * Create a new lobby.
     * @param name the unique name of the lobby
     */
    void createLobby(String name);

    /**
     * Get all lobbies.
     * @return all lobbies
     */
    Collection<Lobby> getAll();

    /**
     * Null-safe way to get a lobby by its name.
     * @param name the lobby name
     * @return the {@link Lobby}. Never null
     * @throws IllegalArgumentException when the lobby cannot be found
     */
    Lobby getLobbySafe(String name) throws IllegalArgumentException;

    /**
     * Set this lobby as fallback / default lobby.
     * @param lobbyName the unique lobby name
     */
    void setDefaultLobby(String lobbyName);

    /**
     * Get the default / fallback lobby name.
     * @return the default / fallback lobby name
     */
    String getDefaultLobbyName();

    /**
     * Get the spawn point of this lobby.
     * @param lobbyName the unique lobby name
     * @return the player spawn point
     */
    PlayerSpawn getLobbySpawn(String lobbyName);

    /**
     * Set the spawn point of this lobby.
     * @param lobbyName the unique lobby name
     * @param spawn the player spawn point
     */
    void setLobbySpawn(String lobbyName, PlayerSpawn spawn);

    /**
     * Add an arena to a lobby.
     * @param lobbyName the unique lobby name
     * @param arenaName the unique arena name
     */
    void addArena(String lobbyName, String arenaName);

    /**
     * Remove an arena from a lobby.
     * @param lobbyName the unique lobby name
     * @param arenaName the unique arena name
     */
    void removeArena(String lobbyName, String arenaName);

    /**
     * Delete a lobby.
     * @param name the unique lobby name
     */
    void deleteLobby(String name);
}
