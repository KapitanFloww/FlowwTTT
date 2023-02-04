package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.game.events.EventSink;
import de.flowwindustries.flowwttt.game.events.listener.reduce.ReductionType;
import de.flowwindustries.flowwttt.game.events.listener.reduce.TTTPlayerReduceEvent;
import de.flowwindustries.flowwttt.repository.ArchivedGameRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.ChestService;
import de.flowwindustries.flowwttt.services.GameManagerService;
import de.flowwindustries.flowwttt.services.RoleService;
import de.flowwindustries.flowwttt.utils.SpigotParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation of {@link GameManagerService}.
 */
@Log
@RequiredArgsConstructor
public class GameManagerServiceImpl implements GameManagerService {

    /**
     * Pointer to the current active instance.
     */
    private GameInstance currentInstance;

    private final ChestService chestService;
    private final ArenaService arenaService;
    private final RoleService roleService;
    private final ArchivedGameRepository archivedGameRepository;
    private final EventSink eventSink;

    private final FileConfigurationWrapper configurationWrapper;

    @Override
    public GameInstance createInstance(Lobby lobby) {
        GameInstance gameInstance = new GameInstance(chestService, arenaService, roleService, this, archivedGameRepository, eventSink, configurationWrapper);
        gameInstance.setLobby(lobby);

        // Update the pointer to the current instance
        this.currentInstance = gameInstance;

        log.info("Created new instance %s in lobby: %s".formatted(gameInstance.getIdentifier(), lobby.getLobbyName()));

        return gameInstance;
    }

    @Override
    public boolean isInCurrentInstance(Player player) {
        boolean isInInstance = currentInstance.getAllPlayers().stream().anyMatch(it -> it.getName().equals(player.getName()));
        log.info("Check if player %s is in current instance %s: %s".formatted(player.getName(), currentInstance.getIdentifier(), isInInstance));
        return isInInstance;
    }

    @Override
    public GameInstance getCurrentInstance() {
        return Optional.ofNullable(currentInstance).orElseThrow(() -> new IllegalStateException("No current instance found!"));
    }

    @Override
    public void start(Arena arena) {
        log.info("Request to start instance: " + currentInstance.getIdentifier() + " in arena: " + arena.getArenaName());
        if(currentInstance.getCurrentStage().getName() != Stage.LOBBY) {
            throw new IllegalStateException("Instance is already running. Current stage: %s".formatted(currentInstance.getCurrentStage().getName()));
        }
        currentInstance.setArena(arena);
        currentInstance.startNext(); // will trigger countdown stage
    }

    @Override
    public void addPlayer(Player player) {
        log.info("Adding player " + player.getName() + " to instance: " + currentInstance.getIdentifier());
        currentInstance.addPlayer(player);
    }

    @Override
    public void deletePlayer(Player player) {
        log.info("Removing player " + player.getName() + " from instance: " + currentInstance.getIdentifier());

        TTTPlayerReduceEvent reduceEvent = new TTTPlayerReduceEvent()
                .withInstance(currentInstance)
                .withReductionType(ReductionType.REMOVAL)
                .withVictim(player)
                .withKiller(null)
                .withTttSourceEvent(null);
        eventSink.push(reduceEvent);

        // Teleport player back to lobby
        var lobbySpawn = currentInstance.getLobby().getLobbySpawn();
        currentInstance.teleport(player, SpigotParser.mapSpawnToLocation(lobbySpawn));
    }

    @Override
    public Stage nextStage() throws IllegalStateException {
        log.info("Triggering next stage of instance: " + currentInstance.getIdentifier());
        if(currentInstance.getCurrentStage().getName() == Stage.LOBBY && currentInstance.getArena() == null) {
            throw new IllegalStateException("Must set an arena first");
        }
        currentInstance.startNext();
        return currentInstance.getCurrentStage().getName();
    }

    @Override
    public void end() {
        log.info("Request to end instance: %s".formatted(currentInstance.getIdentifier()));
        currentInstance.end();
    }

    @Override
    public List<ArchivedGame> listArchived() {
        log.info("Request to list all archived instances");
        return archivedGameRepository.findAll().stream().toList();
    }

    @Override
    public void cleanupInstance() {
        log.info("Request to cleanup current instance: %s".formatted(currentInstance.getIdentifier()));
        currentInstance.cleanup();
    }
}
