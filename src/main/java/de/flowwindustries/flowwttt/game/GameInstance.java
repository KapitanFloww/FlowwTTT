package de.flowwindustries.flowwttt.game;

import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.game.stages.ArchiveGameStage;
import de.flowwindustries.flowwttt.game.stages.CountdownStage;
import de.flowwindustries.flowwttt.game.stages.EndgameStage;
import de.flowwindustries.flowwttt.game.stages.GracePeriodStage;
import de.flowwindustries.flowwttt.game.stages.LobbyStage;
import de.flowwindustries.flowwttt.game.stages.RunningStage;
import de.flowwindustries.flowwttt.repository.ArchivedGameRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.ChestService;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private GameResult gameResult;
    private Lobby lobby;
    private Arena arena;

    private final Map<Player, Role> playerRoles = new HashMap<>();
    private final Map<Player, Role> activePlayers = new HashMap<>();

    private final ChestService chestService;
    private final ArenaService arenaService;
    private final RoleService roleService;
    private final ArchivedGameRepository archivedGameRepository;

    public GameInstance(ChestService chestService, ArenaService arenaService, RoleService roleService, ArchivedGameRepository archivedGameRepository) {
        this.identifier = UUID.randomUUID().toString();
        this.createdAt = Instant.now(Clock.systemUTC());
        this.gameResult = GameResult.PENDING;
        this.chestService = chestService;
        this.arenaService = arenaService;
        this.roleService = roleService;
        this.archivedGameRepository = Objects.requireNonNull(archivedGameRepository);
        this.currentStage = getGameStage(Stage.LOBBY);
        this.currentStage.beginStage();
    }

    public void addPlayer(Player player) {
        if(currentStage.getName() != Stage.LOBBY) {
            throw new IllegalArgumentException("Cannot add player to instance in stage: %s".formatted(currentStage.getName()));
        }
        activePlayers.put(player, Role.PENDING);
        log.config("Adding player %s to game instance %s".formatted(player.getName(), identifier));
    }

    @Deprecated
    public void killPlayer(Player player) {
        var lobbyLocation = SpigotParser.mapSpawnToLocation(lobby.getLobbySpawn());
        teleport(player, lobbyLocation);
        heal(player);
        removePlayer(player);
        notifyPlayer(player, "You have been killed!");
    }

    @Deprecated
    public void removePlayer(Player player) {
        activePlayers.remove(player);
        log.config("Removed player " + player.getName() + " to game instance " +  identifier);
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
     * Trigger the next stage.
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
        log.config("Healing player %s".formatted(player.getName()));
        player.setHealth(20);
    }

    /**
     * Heal all active players in this instance.
     */
    public void healAll() {
        getCurrentPlayersActive().forEach(this::heal);
    }

    /**
     * Clear a players inventory.
     * @param player - the player to clear
     */
    public void clear(Player player) {
        log.config("Clearing inventory of player %s".formatted(player.getName()));
        player.getInventory().clear();
    }

    /**
     * Clear the inventory of all active players of this instance.
     */
    public void clearAll() {
        getCurrentPlayersActive().forEach(this::clear);
    }

    /**
     * Set the game-mode of a specific player.
     * @param player - the target player
     * @param gameMode - the target game-mode
     */
    public void setGameMode(Player player, GameMode gameMode) {
        log.config("Changing game-mode of player %s to: %s".formatted(player.getName(), gameMode));
        player.setGameMode(gameMode);
    }

    /**
     * Set the game-mode of all active players of this instance.
     * @param gameMode - the target game-mode
     */
    public void setGameModeAll(GameMode gameMode) {
        getCurrentPlayersActive().forEach(player -> setGameMode(player, gameMode));
    }

    /**
     * Teleport the given player to a specific location.
     * @param player - the player to teleport
     * @param location - the location to teleport to
     */
    public void teleport(Player player, Location location) {
        log.config("Teleporting player %s to: %s".formatted(player.getName(), location));
        player.teleport(location);
    }

    /**
     * Teleport all active players of this instance.
     * @param location - the target location
     */
    public void teleportAll(Location location) {
        getCurrentPlayersActive().forEach(player -> teleport(player, location));
    }

    /**
     * Set the level of a player.
     * @param player - the target player
     * @param level - the level to set
     */
    public void setLevel(Player player, int level) {
        log.config("Setting level of player %s to: %s".formatted(player.getName(), level));
        player.setLevel(level);
    }

    /**
     * Set the level of all active players of this instance.
     * @param level - the level to set
     */
    public void setLevelAll(int level) {
        getCurrentPlayersActive().forEach(player -> setLevel(player, level));
    }

    /**
     * Notify the given player.
     * @param player - the player to receive the message
     * @param message - the message to send
     */
    public void notifyPlayer(Player player, String message) {
        log.config("Notifying player %s. Message: %s".formatted(player.getName(), message));
        PlayerMessage.info(message, player);
    }

    /**
     * Notify all players of this instance.
     * @param message - the message to broadcast
     */
    public void notifyAllPlayers(String message) {
        getCurrentPlayersActive().forEach(player -> notifyPlayer(player, message));
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
            return new LobbyStage(this, arenaService);
        }
        if(currentStage.getName() == stage) {
            throw new IllegalStateException("Instance is already in stage %s".formatted(currentStage.getName()));
        }
        return switch (stage) {
            case LOBBY -> new LobbyStage(this, arenaService);
            case COUNTDOWN -> new CountdownStage(this, chestService);
            case GRACE_PERIOD -> new GracePeriodStage(this, roleService);
            case RUNNING -> new RunningStage(this);
            case ENDGAME -> new EndgameStage(this, chestService);
            case ARCHIVED -> new ArchiveGameStage(this, archivedGameRepository);
        };
    }
}
