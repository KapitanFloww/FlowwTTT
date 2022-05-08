package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.domain.GameInstance;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.services.GameMasterService;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation of {@link GameMasterService}.
 */
public class GameMasterServiceImpl implements GameMasterService {

    private static final List<GameInstance> instances = new ArrayList<>();
    private static final Map<Player, String> playerInstanceMap = new HashMap<>();

    @Override
    public GameInstance newInstance(Lobby lobby) {
        GameInstance gameInstance = new GameInstance();
        gameInstance.setIdentifier(UUID.randomUUID().toString());
        gameInstance.setLobby(lobby);
        gameInstance.setStage(Stage.LOBBY);
        instances.add(gameInstance);
        return gameInstance;
    }

    @Override
    public GameInstance getGameInstance(String identifier) {
        return null; //TODO #2 implement
    }

    @Override
    public GameInstance getInstanceOf(Player player) {
        return getGameInstanceSafe(playerInstanceMap.get(player));
    }


    @Override
    public void start(String identifier, Arena arena) {
        GameInstance instance = getGameInstanceSafe(identifier);
        instance.setArena(arena);
        instance.setStage(Stage.COUNTDOWN);
    }

    @Override
    public void addPlayer(String identifier, Player player) {
        GameInstance instance = getGameInstanceSafe(identifier);
        instance.addPlayer(player);
        playerInstanceMap.put(player, instance.getIdentifier());
    }

    @Override
    public void deletePlayer(String identifier, Player player) {
        GameInstance instance = getGameInstanceSafe(identifier);
        instance.removePlayer(player);
        playerInstanceMap.remove(player);
    }

    @Override
    public void nextStage(String identifier) {
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
    public void stop(String identifier) {
        //TODO #2 implement stop
    }

    private GameInstance getGameInstanceSafe(String identifier) {
        Optional<GameInstance> optionalGameInstance = instances.stream()
                .filter(instance -> instance.getIdentifier().equalsIgnoreCase(identifier))
                .findFirst();

        if(optionalGameInstance.isEmpty()) {
            throw new IllegalArgumentException("Game instance " + identifier + " does not exist");
        }

        return optionalGameInstance.get();
    }
}
