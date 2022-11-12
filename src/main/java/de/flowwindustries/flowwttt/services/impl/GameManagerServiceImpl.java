package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.GameInstance;
import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.repository.ArchivedGameRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.ChestService;
import de.flowwindustries.flowwttt.services.GameManagerService;
import de.flowwindustries.flowwttt.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static de.flowwindustries.flowwttt.GameInstance.healAndClearPlayers;
import static de.flowwindustries.flowwttt.GameInstance.teleportLobbyAll;

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
    public GameInstance newInstance(Lobby lobby) {
        GameInstance gameInstance = new GameInstance(chestService, arenaService, roleService);
        gameInstance.setIdentifier(UUID.randomUUID().toString());
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
        instance.setArena(arena);
        instance.setStage(Stage.COUNTDOWN);
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
        Stage currentStage = instance.getStage();
        switch (currentStage) {
            case LOBBY -> {
                instance.setStage(Stage.COUNTDOWN);
                return Stage.COUNTDOWN;
            }
            case COUNTDOWN -> {
                instance.setStage(Stage.GRACE_PERIOD);
                return Stage.GRACE_PERIOD;
            }
            case GRACE_PERIOD -> {
                instance.setStage(Stage.RUNNING);
                return Stage.GRACE_PERIOD;
            }
            case RUNNING -> {
                instance.setStage(Stage.ENDGAME);
                return Stage.RUNNING;
            }
            case ENDGAME -> {
                Lobby lobby = instance.getLobby();
                GameInstance nextInstance = newInstance(lobby);
                instances.add(nextInstance);
                Bukkit.getOnlinePlayers().forEach(player -> playerInstanceMap.put(player, nextInstance.getIdentifier()));
                return Stage.LOBBY;
            }
            default -> throw new IllegalStateException("Instance is in invalid stage");
        }
    }

    @Override
    public void end(String identifier) {
        GameInstance instance = getGameInstanceSafe(identifier);

        if(instance.getStage() == Stage.ARCHIVED) {
            return;
        }
        if(instance.getGameResult() == GameResult.CANCELED) {
            return;
        }

        healAndClearPlayers(instance.getCurrentPlayers());
        teleportLobbyAll(instance.getLobby().getLobbySpawn(), instance.getCurrentPlayers());

        List<Integer> instanceTasks = GameManagerService.getInstanceTask(instance.getIdentifier());
        if(instanceTasks != null) {
            instanceTasks.forEach(taskId -> {
                log.info("Canceling task " + taskId);
                Bukkit.getScheduler().cancelTask(taskId);
            });
        }
        GameManagerService.clearTasks(instance.getIdentifier());
        instance.setGameResult(GameResult.CANCELED);

        instance.setStage(Stage.ARCHIVED);
        this.archivedGameRepository.create(new ArchivedGame(identifier,
                instance.getGameResult(),
                instance.getStage(),
                instance.getLobby().getLobbyName(),
                instance.getArena().getArenaName(),
                instance.getCurrentPlayers().toString(),
                Instant.now(Clock.system(ZoneId.systemDefault())))
        );
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
