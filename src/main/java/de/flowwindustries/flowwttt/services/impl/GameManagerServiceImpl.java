package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.game.stages.ArchiveGameStage;
import de.flowwindustries.flowwttt.repository.ArchivedGameRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.ChestService;
import de.flowwindustries.flowwttt.services.GameManagerService;
import de.flowwindustries.flowwttt.services.RoleService;
import de.flowwindustries.flowwttt.utils.SpigotParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service implementation of {@link GameManagerService}.
 */
@Log
@RequiredArgsConstructor
public class GameManagerServiceImpl implements GameManagerService {

    private static final List<GameInstance> instances = new ArrayList<>();
    private static final Map<Player, String> playerInstanceMap = new HashMap<>();

    private final ChestService chestService;
    private final ArenaService arenaService;
    private final RoleService roleService;
    private final ArchivedGameRepository archivedGameRepository;

    @Override
    public GameInstance createInstance(Lobby lobby) {
        GameInstance gameInstance = new GameInstance(chestService, arenaService, roleService, archivedGameRepository);
        gameInstance.setLobby(lobby);
        instances.add(gameInstance);
        log.info("Created " +
                "new game instance with id: " + gameInstance.getIdentifier());
        return gameInstance;
    }

    @Override
    public GameInstance getInstanceOf(Player player) {
        log.config("Request to get instance of player: " + player.getName());
        return getGameInstance(playerInstanceMap.get(player));
    }


    @Override
    public void start(String identifier, Arena arena) {
        log.info("Request to start instance: " + identifier + " in arena: " + arena.getArenaName());
        GameInstance instance = getGameInstanceSafe(identifier);

        if(instance.getCurrentStage().getName() != Stage.LOBBY) {
            throw new IllegalStateException("Instance is already running. Current stage: %s".formatted(instance.getCurrentStage().getName()));
        }
        instance.setArena(arena);
        instance.startNext(); // will trigger lobby countdown
    }

    @Override
    public void addPlayer(String identifier, Player player) {
        log.info("Adding player " + player.getName() + " to instance: " + identifier);
        GameInstance instance = getGameInstanceSafe(identifier);
        instance.addPlayer(player);
        playerInstanceMap.put(player, instance.getIdentifier());
    }

    @Override
    public void deletePlayer(String identifier, Player player) {
        log.info("Removing player " + player.getName() + " from instance: " + identifier);
        GameInstance instance = getGameInstanceSafe(identifier);
        instance.removePlayer(player);
        playerInstanceMap.remove(player);
    }

    @Override
    public Stage nextStage(String identifier) throws IllegalStateException {
        log.info("Triggering next stage of instance: " + identifier);
        GameInstance instance = getGameInstanceSafe(identifier);
        instance.startNext();
        return instance.getCurrentStage().getName();
    }

    @Override
    public void end(String identifier) {
        GameInstance instance = getGameInstanceSafe(identifier);
        if(instance.getCurrentStage().getName() == Stage.ARCHIVED) {
            throw new IllegalArgumentException("Instance is already archived: %s".formatted(instance.getIdentifier()));
        }
        // End the current stage
        instance.getCurrentStage().endStage();
        // Cancel the match
        instance.setGameResult(GameResult.CANCELED);
        instance.setCurrentStage(new ArchiveGameStage(instance, archivedGameRepository));
        instance.healAll();
        instance.clearAll();
        instance.setGameModeAll(GameMode.ADVENTURE);
        var lobbyLocation = SpigotParser.mapSpawnToLocation(instance.getLobby().getLobbySpawn());
        instance.teleportAll(lobbyLocation);
    }

    @Override
    public Collection<GameInstance> list() {
        log.info("Request to list all instances");
        return instances;
    }

    @Override
    public GameInstance getGameInstanceSafe(String identifier) {
        log.config("Request to get instance with identifier: " + identifier);
        return Optional.ofNullable(getGameInstance(identifier))
                .orElseThrow(() -> new IllegalArgumentException("No game instance found for identifier: " + identifier));
    }

    @Override
    public Collection<ArchivedGame> listArchived() {
        log.config("Request to list all archived instances");
        return archivedGameRepository.findAll();
    }

    private GameInstance getGameInstance(String identifier) {
        List<GameInstance> instanceList = instances.stream()
                .filter(instance -> instance.getIdentifier().equalsIgnoreCase(identifier))
                .toList();
        if(instanceList.isEmpty()) {
            return null;
        }
        if(instanceList.size() > 1) {
            throw new IllegalStateException("More than one instance with identifier " + identifier + " found");
        }
        return instanceList.get(0);
    }
}
