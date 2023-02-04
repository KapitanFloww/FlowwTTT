package de.flowwindustries.flowwttt.game;

import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.game.events.listener.reduce.ReductionType;
import de.flowwindustries.flowwttt.game.events.EventSink;
import de.flowwindustries.flowwttt.game.stages.ArchiveGameStage;
import de.flowwindustries.flowwttt.game.stages.CountdownStage;
import de.flowwindustries.flowwttt.game.stages.EndgameStage;
import de.flowwindustries.flowwttt.game.stages.GameStage;
import de.flowwindustries.flowwttt.game.stages.GracePeriodStage;
import de.flowwindustries.flowwttt.game.stages.LobbyStage;
import de.flowwindustries.flowwttt.game.stages.RunningStage;
import de.flowwindustries.flowwttt.repository.ArchivedGameRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.ChestService;
import de.flowwindustries.flowwttt.services.GameManagerService;
import de.flowwindustries.flowwttt.services.RoleService;
import de.flowwindustries.flowwttt.utils.SpigotParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A game instance. Refers to one specific match.
 */
@Log
@Getter
@Setter
public class GameInstance {

    private final String identifier;
    private final Instant createdAt;
    private GameStage currentStage;
    private GameResult gameResult = GameResult.PENDING;
    private Lobby lobby;
    private Arena arena;

    private final Set<Player> allPlayers = new HashSet<>();
    private final Map<Player, Role> playerRoles = new HashMap<>();
    private final Map<Player, Role> activePlayers = new HashMap<>();
    private final Map<Player, ReductionType> removedPlayers = new HashMap<>();

    private final ChestService chestService;
    private final ArenaService arenaService;
    private final RoleService roleService;
    private final GameManagerService gameManagerService;
    private final ArchivedGameRepository archivedGameRepository;

    private final FileConfigurationWrapper configurationWrapper;

    private final EventSink eventSink;

    public GameInstance(ChestService chestService, ArenaService arenaService, RoleService roleService,
                        GameManagerService gameManagerService, ArchivedGameRepository archivedGameRepository, EventSink eventSink,
                        FileConfigurationWrapper configurationWrapper) {
        this.configurationWrapper = Objects.requireNonNull(configurationWrapper);
        this.archivedGameRepository = Objects.requireNonNull(archivedGameRepository);
        this.gameManagerService = Objects.requireNonNull(gameManagerService);
        this.chestService = Objects.requireNonNull(chestService);
        this.arenaService = Objects.requireNonNull(arenaService);
        this.roleService = Objects.requireNonNull(roleService);
        this.eventSink = Objects.requireNonNull(eventSink);
        this.identifier = UUID.randomUUID().toString();
        this.createdAt = Instant.now(Clock.systemUTC());
        init();
    }

    private void init() {
        this.currentStage = getGameStage(Stage.LOBBY);
        this.currentStage.beginStage();
    }

    /**
     * Add a player to this instance.
     * @param player - the player to add
     */
    public void addPlayer(Player player) {
        if(currentStage.getName() != Stage.LOBBY) {
            throw new IllegalArgumentException("Cannot add player to instance in stage: %s".formatted(currentStage.getName()));
        }
        allPlayers.add(player);
        activePlayers.put(player, Role.PENDING);
        setLevel(player, 0);
        setGameMode(player, GameMode.ADVENTURE);
        clearInventory(player);
        heal(player);
        feed(player);
        log.info("Added player %s to game instance %s".formatted(player.getName(), identifier));
    }

    /**
     * Remove & kill a player from this instance.
     * @param player - the player to remove
     * @param reductionType - the reduction type
     */
    public void removePlayer(Player player, ReductionType reductionType) {
        killPlayer(player, reductionType);
        allPlayers.remove(player);
        log.info("Removed player " + player.getName() + " from game instance " +  identifier);
    }

    /**
     * Kill a player but do not remove player from instance.
     * @param player - the player to kill
     * @param reductionType - the reduction type
     */
    public void killPlayer(Player player, ReductionType reductionType) {
        activePlayers.remove(player);
        removedPlayers.put(player, reductionType);
        log.info("Killed player " + player.getName() + " in game instance " +  identifier);
    }

    /**
     * Start this game instance.
     */
    public void start() {
        if(currentStage != null) {
            throw new IllegalStateException("Instance is already in stage: %s".formatted(currentStage.getName()));
        }
        log.info("Creating new stage lifecycle for instance %s".formatted(identifier));
        GameStage lobbyStage = getGameStage(Stage.LOBBY);
        lobbyStage.beginStage();
    }

    /**
     * End the current stage and trigger the next stage.
     */
    public void startNext() {
        // End current stage
        currentStage.endStage();
        // Trigger next stage
        var stage = currentStage.getNext();
        if(stage != null) {
            GameStage nextStage = getGameStage(stage);
            this.currentStage = nextStage;
            nextStage.beginStage();
            return;
        }
        log.info("Instance at end for instance: %s".formatted(identifier)); // TODO KapitanFloww clean-up?
    }

    /**
     * Heal a specific player.
     * @param player - the player to heal
     */
    public void heal(Player player) {
        log.info("Healing player %s".formatted(player.getName()));
        player.setHealth(20);
    }

    /**
     * Heal all active players in this instance.
     */
    public void healAll() {
        allPlayers.forEach(this::heal);
    }

    /**
     * Clear a players inventory.
     * @param player - the player to clear
     */
    public void clearInventory(Player player) {
        log.info("Clearing inventory of player %s".formatted(player.getName()));
        player.getInventory().clear();
    }

    /**
     * Clear the inventory of all players of this instance.
     */
    public void clearInventoryAll() {
        allPlayers.forEach(this::clearInventory);
    }

    /**
     * Set the game-mode of a specific player.
     * @param player - the target player
     * @param gameMode - the target game-mode
     */
    public void setGameMode(Player player, GameMode gameMode) {
        log.info("Changing game-mode of player %s to: %s".formatted(player.getName(), gameMode));
        player.setGameMode(gameMode);
    }

    /**
     * Set the game-mode of all players of this instance.
     * @param gameMode - the target game-mode
     */
    public void setGameModeAll(GameMode gameMode) {
        allPlayers.forEach(player -> setGameMode(player, gameMode));
    }

    /**
     * Teleport the given player to a specific location.
     * @param player - the player to teleport
     * @param location - the location to teleport to
     */
    public void teleport(Player player, Location location) {
        log.info("Teleporting player %s to: %s".formatted(player.getName(), location));
        player.teleport(location);
    }

    /**
     * Teleport all players of this instance.
     * @param location - the target location
     */
    public void teleportAll(Location location) {
        allPlayers.forEach(player -> teleport(player, location));
    }

    /**
     * Set the level of a player.
     * @param player - the target player
     * @param level - the level to set
     */
    public void setLevel(Player player, int level) {
        log.info("Setting level of player %s to: %s".formatted(player.getName(), level));
        player.setLevel(level);
    }

    /**
     * Set the level of all players of this instance.
     * @param level - the level to set
     */
    public void setLevelAll(int level) {
        allPlayers.forEach(player -> setLevel(player, level));
    }

    /**
     * Notify the given player.
     * @param player - the player to receive the message
     * @param message - the message to send
     */
    public void notifyPlayer(Player player, String message) {
        log.info("Notifying player %s. Message: %s".formatted(player.getName(), message));
        PlayerMessage.info(message, player);
    }

    /**
     * Notify all players of this instance.
     * @param message - the message to broadcast
     */
    public void notifyAllPlayers(String message) {
        allPlayers.forEach(player -> notifyPlayer(player, message));
    }

    /**
     * Feed a player.
     * @param player - the player to feed
     */
    public void feed(Player player) {
        log.info("Feeding player %s".formatted(player.getName()));
        player.setFoodLevel(20);
    }

    /**
     * Get the current active players count.
     * @return - the amount of active players of this instance
     */
    public int getCurrentPlayerCount() {
        return getCurrentPlayersActive().size();
    }

    /**
     * Get all active players of this instance.
     * @return a {@link List} of active {@link Player}s of this instance
     */
    public List<Player> getCurrentPlayersActive() {
        return activePlayers.keySet().stream().toList();
    }

    private GameStage getGameStage(Stage stage) {
        if(currentStage == null) {
            return new LobbyStage(this, arenaService, eventSink, configurationWrapper);
        }
        if(currentStage.getName() == stage) {
            throw new IllegalStateException("Instance is already in stage %s".formatted(currentStage.getName()));
        }
        return switch (stage) {
            case LOBBY -> new LobbyStage(this, arenaService, eventSink, configurationWrapper);
            case COUNTDOWN -> new CountdownStage(this, chestService, configurationWrapper);
            case GRACE_PERIOD -> new GracePeriodStage(this, roleService, configurationWrapper);
            case RUNNING -> new RunningStage(this, configurationWrapper);
            case ENDGAME -> new EndgameStage(this, chestService);
            case ARCHIVED -> new ArchiveGameStage(this, gameManagerService, archivedGameRepository);
        };
    }

    /**
     * Cancel and end this instance.
     * @throws IllegalArgumentException if this instance is already in {@link Stage#ARCHIVED}.
     */
    public void end() throws IllegalArgumentException {
        if(getCurrentStage().getName() == Stage.ARCHIVED) {
            throw new IllegalArgumentException("Instance is already archived: %s".formatted(getIdentifier()));
        }
        // End the current stage
        getCurrentStage().endStage();
        // Cancel the match
        setGameResult(GameResult.CANCELED);
        setCurrentStage(new ArchiveGameStage(this, gameManagerService, archivedGameRepository));
        healAll();
        clearInventoryAll();
        setGameModeAll(GameMode.ADVENTURE);
        var lobbyLocation = SpigotParser.mapSpawnToLocation(getLobby().getLobbySpawn());
        teleportAll(lobbyLocation);

        // Try to de-spawn chests
        chestService.despawnChests(this.getArena());
    }

    /**
     * Clear this instance.
     */
    public void cleanup() {
        log.info("Clearing instance %s".formatted(identifier));
        allPlayers.clear();
        activePlayers.clear();
        removedPlayers.clear();
        playerRoles.clear();
    }
}
