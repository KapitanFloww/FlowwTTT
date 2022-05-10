package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.domain.GameInstance;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation of {@link GameManagerService}.
 */
@Log
public class GameManagerServiceImpl implements GameManagerService {

    private static final List<GameInstance> instances = new ArrayList<>();
    private static final Map<Player, String> playerInstanceMap = new HashMap<>();

    @Override
    public GameInstance newInstance(Lobby lobby) {
        GameInstance gameInstance = new GameInstance();
        gameInstance.setIdentifier(UUID.randomUUID().toString());
        gameInstance.setLobby(lobby);
        gameInstance.setStage(Stage.LOBBY);
        instances.add(gameInstance);
        log.info("Created new game instance with id: " + gameInstance.getIdentifier());
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
    public void nextStage(String identifier) {
        log.info("Triggering next stage of instance: " + identifier);
        GameInstance instance = getGameInstanceSafe(identifier);
        Stage currentStage = instance.getStage();
        switch (currentStage) {
            case LOBBY -> instance.setStage(Stage.COUNTDOWN);
            case COUNTDOWN -> instance.setStage(Stage.GRACE_PERIOD);
            case GRACE_PERIOD -> instance.setStage(Stage.RUNNING);
            case RUNNING -> instance.setStage(Stage.ENDGAME);
            case ENDGAME -> instance.setStage(Stage.LOBBY);
        }
    }

    @Override
    public void end(String identifier) {
        //TODO #2 implement end
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
