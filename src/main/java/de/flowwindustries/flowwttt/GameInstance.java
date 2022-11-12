package de.flowwindustries.flowwttt;

import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import de.flowwindustries.flowwttt.scheduled.Countdown;
import de.flowwindustries.flowwttt.scheduled.Idler;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.ChestService;
import de.flowwindustries.flowwttt.services.RoleService;
import de.flowwindustries.flowwttt.utils.SpigotParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_GRACE_PERIOD_DURATION;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_LOBBY_COUNTDOWN_DURATION;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_MAX_DURATION;
import static de.flowwindustries.flowwttt.config.FileConfigurationWrapper.readInt;
import static de.flowwindustries.flowwttt.utils.SpigotParser.mapSpawnToLocation;

/**
 * A game instance.
 * I.e: a match
 */
@Log
public class GameInstance {

    public final int maxGameDuration;
    public final int lobbyCountDownDuration;
    public final int gracePeriodDuration;

    @Getter
    private String identifier;

    @Getter
    private Stage stage;

    @Getter
    @Setter
    private GameResult gameResult;

    @Getter
    @Setter
    private Arena arena;

    @Getter
    @Setter
    private Lobby lobby;

    private final Map<Player, Role> playerRoles = new HashMap<>();

    private final Map<Player, Role> activePlayers = new HashMap<>();

    private final ChestService chestService;
    private final ArenaService arenaService;
    private final RoleService roleService;

    public GameInstance(ChestService chestService, ArenaService arenaService, RoleService roleService) {
        this.identifier = UUID.randomUUID().toString();
        this.maxGameDuration = readInt(PATH_GAME_MAX_DURATION);
        this.lobbyCountDownDuration = readInt(PATH_GAME_LOBBY_COUNTDOWN_DURATION);
        this.gracePeriodDuration = readInt(PATH_GAME_GRACE_PERIOD_DURATION);
        this.chestService = chestService;
        this.arenaService = arenaService;
        this.roleService = roleService;
        this.setStage(Stage.LOBBY);
    }

    public void addPlayer(Player player) {
        activePlayers.put(player, Role.PENDING);
        log.config("Added player " + player.getName() + " to game instance " + identifier);
    }

    public void killPlayer(Player player) {
        var lobbyLocation = SpigotParser.mapSpawnToLocation(lobby.getLobbySpawn());
        PlayerMessage.info("You have been killed!", player);
        player.setHealth(20.0d);
        player.teleport(lobbyLocation);

        removePlayer(player);
        recalculateGameStats();
    }

    public void removePlayer(Player player) {
        activePlayers.remove(player);
        log.config("Removed player " + player.getName() + " to game instance " +  identifier);
    }

    private void recalculateGameStats() {
        // Check for traitor win
        var traitors = getCurrentPlayersActive().stream().filter(player -> activePlayers.get(player) == Role.TRAITOR).toList();
        if(traitors.size() == 0) {
            initializeInnoWin();
            return;
        }
        // Check for inno win
        var innocents = getCurrentPlayersActive().stream().filter(player -> activePlayers.get(player) == Role.INNOCENT).toList();
        var detective = getCurrentPlayersActive().stream().filter(player -> activePlayers.get(player) == Role.DETECTIVE).toList();
        if(innocents.size() + detective.size() == 0) {
            initializeTraitorWin();
            return;
        }
        throw new IllegalStateException("Invalid game ending!");
    }

    private void initializeInnoWin() {
        // TODO KapitanFloww set game result + endgame
    }

    private void initializeTraitorWin() {
        // TODO KapitanFloww set game result + endgame
    }

    public void setStage(Stage stage) {
        if(this.stage == stage) {
            return;
        }
        switch (stage) {
            case LOBBY -> initializeLobby(); // Teleport all players to lobby, set gamemode to adventure
            case COUNTDOWN -> initializeCountdown(); // Lobby countdown
            case GRACE_PERIOD -> initializeGracePeriod(); // Count-down grace-period and then assign roles
            case RUNNING -> initializeRunning(); // Disable grace period, keep track of the players
            case ENDGAME -> initializeEndgame(); // 10s countdown to display the results and winner, remove chests, teleport players
            case ARCHIVED -> log.info("Archiving game instance " + identifier);
        }
        this.stage = stage;
    }

    private void initializeCountdown() {
        log.info(String.format("Initializing %s stage", Stage.COUNTDOWN));
        for(int i=0; i < getCurrentPlayersActive().size(); i++) {
            Player player = getCurrentPlayersActive().get(i);
            PlayerSpawn spawn = arena.getPlayerSpawns().get(i);

            World world = Bukkit.getWorld(spawn.getWorldName());
            Location location = new Location(world, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());

            player.teleport(location);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        }
        Countdown countdown = new Countdown(TTTPlugin.getInstance(),
                identifier,
                lobbyCountDownDuration,
                () -> PlayerMessage.info("The match will start soon! Get ready!", getCurrentPlayersActive()),
                () -> {
                    PlayerMessage.info("The match has started!", getCurrentPlayersActive());
                    setStage(Stage.GRACE_PERIOD);
                },
                t -> PlayerMessage.info("Match will start in " + t.getTimeLeft() + " seconds", getCurrentPlayersActive()));
        countdown.scheduleCountdown();
        chestService.spawnChests(arena);
    }

    private void initializeGracePeriod() {
        log.info(String.format("Initialize %s stage", Stage.GRACE_PERIOD));
        Countdown countdown = new Countdown(TTTPlugin.getInstance(),
                identifier,
                gracePeriodDuration,
                () -> PlayerMessage.info("Grace Period has started! Roles will be assigned in %s seconds", getCurrentPlayersActive()),
                () -> {
                    PlayerMessage.info("Grace Period has ended!", getCurrentPlayersActive());
                    initializeRoles();
                    setStage(Stage.RUNNING);
                },
                t -> PlayerMessage.info("Grace Period ends in " + t.getTimeLeft() + " seconds", getCurrentPlayersActive()));
        countdown.scheduleCountdown();
    }

    private void initializeRoles() {
        log.config("Assigning roles for " + getPlayerAmount() + " players");
        var roleAssignment = roleService.assignRoles(getCurrentPlayersActive().stream()
                .map(Player::getName)
                .toList());
        roleAssignment.forEach((playerName, role) -> playerRoles.put(mapPlayer(playerName), role));
        notifyPlayerRole();
        log.info("Roles have been assigned");
    }

    private void initializeRunning() {
        log.info(String.format("Initialize %s stage", Stage.RUNNING));
        Countdown countdown = new Countdown(TTTPlugin.getInstance(),
                identifier,
                maxGameDuration,
                () -> {},
                () -> {
                    PlayerMessage.info("Time has run out. Innocents win!", getCurrentPlayersActive());
                    getCurrentPlayersActive().forEach(player -> player.setLevel(0));
                    gameResult = GameResult.TIME_OUT;
                    setStage(Stage.ENDGAME);
                },
                t -> getCurrentPlayersActive().forEach(player -> player.setLevel(t.getTimeLeft()))
        );
        countdown.scheduleCountdown();
    }

    private void initializeEndgame() {
        chestService.deSpawnChests(arena);
        Countdown countdown = new Countdown(TTTPlugin.getInstance(),
                identifier,
                10,
                () -> {
                    PlayerMessage.success("*********************************", getCurrentPlayersActive());
                    if(gameResult == GameResult.INNOCENT_WIN || gameResult == GameResult.TIME_OUT) {
                        PlayerMessage.success("The INNOCENTS win", getCurrentPlayersActive());
                    } else {
                        PlayerMessage.warn("The TRAITORS win", getCurrentPlayersActive());
                    }
                    healAndClearPlayers(getCurrentPlayersActive());
                    teleportLobbyAll(lobby.getLobbySpawn(), getCurrentPlayersActive());
                },
                () -> {
                    setStage(Stage.ARCHIVED);
                    // TODO create a new game instance and assign all online players to it
                },
                t -> {}
        );
        countdown.scheduleCountdown();
    }

    private void notifyPlayerRole() {
        playerRoles.forEach((player, role) -> {
            PlayerMessage.success("########################", player);
            PlayerMessage.success("You are %s".formatted(role), player);
            PlayerMessage.success("########################", player);
        });
    }

    private Player mapPlayer(String playerName) {
        var playerResult = getCurrentPlayersActive().stream().filter(it -> it.getName().equals(playerName)).toList();
        if(playerResult.size() == 1) {
            return playerResult.get(0);
        }
        throw new IllegalStateException("Player %s not found in active players".formatted(playerName));
    }

    public static void teleportLobbyAll(PlayerSpawn lobbySpawn, Collection<Player> players) {
        Location lobbyLocation = mapSpawnToLocation(lobbySpawn);
        players.forEach(player -> player.teleport(lobbyLocation));
    }

    public static void healAndClearPlayers(Collection<Player> players) {
        players.forEach(player -> {
            player.setHealth(20);
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
        });
    }

    private void initializeLobby() {
        Idler lobbyIdler = new Idler(TTTPlugin.getInstance(), this, arenaService);
        lobbyIdler.scheduleIdler();
    }

    public int getPlayerAmount() {
        return this.activePlayers.keySet().size();
    }

    public List<Player> getCurrentPlayersActive() {
        return this.activePlayers.keySet().stream().toList();
    }
}
