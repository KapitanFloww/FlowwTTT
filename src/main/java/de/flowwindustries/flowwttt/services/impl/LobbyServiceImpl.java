package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.config.ConfigurationUtils;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import de.flowwindustries.flowwttt.repository.LobbyRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.LobbyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Collection;
import java.util.Optional;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_DEFAULT_LOBBY;

@Log
@RequiredArgsConstructor
public class LobbyServiceImpl implements LobbyService {

    public static final String LOBBY_NOT_FOUND = "Lobby with name %s does not exist";
    public static final String LOBBY_ALREADY_EXISTS = "Lobby with name %s does already exist";
    private static final String INVALID_NAME = "Invalid lobby name: %s";
    private final LobbyRepository lobbyRepository;
    private final ArenaService arenaService;

    @Override
    public void createLobby(String lobbyName) {
        if(lobbyName.equalsIgnoreCase("help")) {
            throw new IllegalArgumentException(String.format(INVALID_NAME, lobbyName));
        }
        if(lobbyName.equalsIgnoreCase("list")) {
            throw new IllegalArgumentException(String.format(INVALID_NAME, lobbyName));
        }
        if(lobbyRepository.find(lobbyName).isPresent()) {
            throw new IllegalArgumentException(String.format(LOBBY_ALREADY_EXISTS, lobbyName));
        }
        log.info("Creating new lobby: " + lobbyName);
        Lobby lobby = new Lobby();
        lobby.setLobbyName(lobbyName);
        lobby.setLobbySpawn(null);
        lobbyRepository.create(lobby);
    }

    @Override
    public Collection<Lobby> getAll() {
        log.info("Request to find all lobbies");
        return lobbyRepository.findAll();
    }

    @Override
    public void setDefaultLobby(String lobbyName) {
        log.info("Request to set default lobby name: " + lobbyName);
        ConfigurationUtils.write(PATH_DEFAULT_LOBBY, lobbyName);
    }

    @Override
    public String getDefaultLobbyName() {
        return ConfigurationUtils.read(String.class, PATH_DEFAULT_LOBBY);
    }

    @Override
    public PlayerSpawn getLobbySpawn(String lobbyName) {
        log.info("Request to get lobby spawn of lobby: " + lobbyName);
        Lobby lobby = getLobbySafe(lobbyName);
        return lobby.getLobbySpawn();
    }

    @Override
    public void setLobbySpawn(String lobbyName, PlayerSpawn spawn) {
        log.info("Request to set lobby spawn of lobby: " + lobbyName + " to: " + spawn);
        Lobby lobby = getLobbySafe(lobbyName);
        lobby.setLobbySpawn(spawn);
        lobbyRepository.edit(lobby);
    }

    @Override
    public void addArena(String lobbyName, String arenaName) {
        Lobby lobby = getLobbySafe(lobbyName);
        Arena arena = arenaService.getArenaSafe(arenaName);

        Collection<Arena> arenas = lobby.getArenas();
        arenas.add(arena);

        lobby.setArenas(arenas);
        lobbyRepository.edit(lobby);
    }

    @Override
    public void removeArena(String lobbyName, String arenaName) {
        Lobby lobby = getLobbySafe(lobbyName);
        Collection<Arena> arenas = lobby.getArenas();
        Optional<Arena> optionalArena = arenas.stream()
                .filter(arena1 -> arena1.getArenaName().equalsIgnoreCase(arenaName))
                .findAny();
        if(optionalArena.isEmpty()) {
            throw new IllegalArgumentException("Arena is not linked to this lobby");
        }
        if(!arenas.remove(optionalArena.get())) {
            throw new IllegalArgumentException("Could not remove arena from lobby");
        }
        lobby.setArenas(arenas);
        lobbyRepository.edit(lobby);
    }

    @Override
    public void deleteLobby(String lobbyName) {
        log.info("Request to delete lobby: " + lobbyName);
        Lobby lobby = getLobbySafe(lobbyName);
        lobbyRepository.remove(lobby);
    }

    private Lobby getLobbySafe(String lobbyName) {
        log.info("Request to get lobby: " + lobbyName);
        return lobbyRepository.find(lobbyName)
                .orElseThrow(() -> new IllegalArgumentException(String.format(LOBBY_NOT_FOUND, lobbyName)));
    }
}
