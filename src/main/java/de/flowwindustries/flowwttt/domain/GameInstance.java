package de.flowwindustries.flowwttt.domain;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.config.ConfigurationUtils;
import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import de.flowwindustries.flowwttt.scheduled.Countdown;
import de.flowwindustries.flowwttt.services.ChestService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_MAX_DURATION;
import static de.flowwindustries.flowwttt.utils.SpigotParser.mapSpawnToLocation;

/**
 * A game instance.
 * I.e: a match
 */
@Log
public class GameInstance {

    public static final int GAME_DURATION = ConfigurationUtils.read(Integer.class, PATH_GAME_MAX_DURATION);
    /**
     * This game's identifier.
     */
    @Getter
    @Setter
    private String identifier;

    /**
     * The current stage this game is in.
     */
    @Getter
    private Stage stage;

    /**
     * The result this game instance has ended.
     */
    @Setter
    @Getter
    private GameResult gameResult;

    /**
     * The arena this game takes place.
     */
    @Setter
    @Getter
    private Arena arena;

    /**
     * The lobby the players will be taken after finishing the game.
     */
    @Setter
    @Getter
    private Lobby lobby;

    /**
     * Map of players and their role.
     */
    private Map<Player, Role> playerRoles;

    /**
     * Current players.
     */
    private final List<Player> players = new ArrayList<>();

    /**
     * Chest Service.
     */
    private final ChestService chestService;

    public GameInstance(ChestService chestService) {
        this.chestService = chestService;
    }

    /**
     * Initialize the roles for the current players.
     */
    public void initializeRoles() {
        int playerSize = players.size();
        log.config("Assigning roles for " + playerSize + " players");

        // TODO #4: Assign roles
        // Call RoleService and fetch the map of roles and players
        // 10% Detective
        // 60% Innocent
        // 30% Traitor
    }

    /**
     * Add a player to this game instance.
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        players.add(player);
        log.config("Added player " + player.getName() + " to game instance " + this.getIdentifier());
    }

    /**
     * Remove a player from this game instance.
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        players.remove(player);
        log.config("Removed player " + player.getName() + " to game instance " +  this.getIdentifier());
    }

    /**
     * Get the current player count of this instance.
     * @return the current player count
     */
    public Collection<Player> getCurrentPlayers() {
        return this.players;
    }

    /**
     * Set the stage of this game instance.
     * @param stage the stage to set this instance to
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        switch (stage) { //TODO #2 implement logic
            case LOBBY -> System.out.println("Returning to lobby"); // Teleport all players to lobby, set gamemode to adventure
            case COUNTDOWN -> initializeCountdown();
            case GRACE_PERIOD -> initializeGracePeriod();
            case RUNNING -> initializeRunning(); // Disable grace period, keep track of the players
            case ENDGAME -> initializeEndgame(); // 10s countdown to display the results and winner, remove chests, teleport players
        }
    }

    private void initializeCountdown() {
        // Teleport players to their spawns
        log.info(String.format("Initializing %s stage", Stage.COUNTDOWN));
        for(int i=0; i<this.players.size(); i++) {
            Player player = this.players.get(i);
            PlayerSpawn spawn = this.getArena().getPlayerSpawns().get(i);

            World world = Bukkit.getWorld(spawn.getWorldName());
            Location location = new Location(world, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());

            player.teleport(location);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        }
        // Count down from 30s
        Countdown countdown = new Countdown(TTTPlugin.getInstance(),
                30,
                () -> PlayerMessage.info("The match will start soon! Get ready!", getCurrentPlayers()),
                () -> {
                    PlayerMessage.info("The match has started!", getCurrentPlayers());
                    this.setStage(Stage.GRACE_PERIOD);
                },
                t -> PlayerMessage.info("Match will start in " + t.getTimeLeft() + " seconds", getCurrentPlayers()));
        countdown.scheduleCountdown();
        chestService.spawnChests(this.getArena());
    }

    private void initializeGracePeriod() {
        log.info(String.format("Initialize %s stage", Stage.GRACE_PERIOD));
        // Count down from 30s
        Countdown countdown = new Countdown(TTTPlugin.getInstance(),
                30,
                () -> PlayerMessage.info("Grace Period has started!", getCurrentPlayers()),
                () -> {
                    PlayerMessage.info("Grace Period has ended!", getCurrentPlayers());
                    this.setStage(Stage.RUNNING);
                },
                t -> PlayerMessage.info("Grace Period ends in " + t.getTimeLeft() + " seconds", getCurrentPlayers()));
        countdown.scheduleCountdown();
    }

    private void initializeRunning() {
        log.info(String.format("Initialize %s stage", Stage.RUNNING));
        Countdown countdown = new Countdown(TTTPlugin.getInstance(),
                GAME_DURATION,
                () -> {},
                () -> {
                    PlayerMessage.info("Time has run out. Innocents win!", this.getCurrentPlayers());
                    this.getCurrentPlayers().forEach(player -> player.setLevel(0));
                    this.setGameResult(GameResult.TIME_OUT);
                    this.setStage(Stage.ENDGAME);
                },
                t -> this.getCurrentPlayers().forEach(player -> player.setLevel(t.getTimeLeft()))
        );
        countdown.scheduleCountdown();

        // TODO
        // Monitor player deaths
        // When killer traitor and victim innocent or detective == good
        // When killer traitor and victim traitor == not good
        // When killer innocent and victim innocent or detective == not good
        // When killer innocent and victim traitor == very good

        // When all killer dead == inno win or by time
        // When all inno dead = traitor win
    }

    private void initializeEndgame() {
        this.chestService.deSpawnChests(this.getArena());
        GameResult result = this.getGameResult();

        Countdown countdown = new Countdown(TTTPlugin.getInstance(),
                10,
                () -> PlayerMessage.success("*********************************", this.getCurrentPlayers()),
                () -> {
                    // TODO set karma
                    this.setStage(Stage.LOBBY);
                    this.getCurrentPlayers().forEach(player -> {
                        Location lobbyLocation = mapSpawnToLocation(this.getLobby().getLobbySpawn());
                        player.teleport(lobbyLocation);
                        player.setHealth(20);
                        player.getInventory().clear();
                        player.setGameMode(GameMode.ADVENTURE);
                    });
                },
                t -> {
                    if(result == GameResult.INNOCENT_WIN || result == GameResult.TIME_OUT) {
                        PlayerMessage.success("The INNOCENTS win", this.getCurrentPlayers());
                    } else {
                        PlayerMessage.warn("The TRAITORS win", this.getCurrentPlayers());
                    }
                }
        );
        countdown.scheduleCountdown();
    }
}
