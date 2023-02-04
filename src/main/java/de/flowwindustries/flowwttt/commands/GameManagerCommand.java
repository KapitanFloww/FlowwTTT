package de.flowwindustries.flowwttt.commands;

import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.GameManagerService;
import de.flowwindustries.flowwttt.services.LobbyService;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.YELLOW;

/**
 * Command class for {@code /gm}.
 */
@Log
public class GameManagerCommand extends AbstractCommand {

    private final ArenaService arenaService;
    private final LobbyService lobbyService;
    private final GameManagerService gameManagerService;

    public GameManagerCommand(String permission, ArenaService arenaService, LobbyService lobbyService, GameManagerService gameManagerService) {
        super(permission);
        this.arenaService = arenaService;
        this.lobbyService = lobbyService;
        this.gameManagerService = gameManagerService;
    }

    @Override
    protected boolean playerCommand(Player player, String[] args) {
        switch (args.length) {
            case 1 -> {
                switch (args[0]) {
                    case "help" -> showHelp(player);
                    case "info" -> infoInstance(player);
                    case "list" -> listArchivedInstances(player);
                    case "nextstage" -> nextStageInstance(player);
                    case "stop" -> stopInstance(player);
                    default -> throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, args[0]));
                }
            }
            case 2 -> {
                switch (args[0]) {
                    case "start" -> startInstance(args[1]);
                    case "addplayer" -> addPlayer(player, args[1]);
                    case "removeplayer" -> removePlayer(player, args[1]);
                    case "create" -> createInstance(player, args[1]);
                    default -> throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, args[0]));
                }
            }
            default -> throw new IllegalArgumentException("See /gm help");
        }
        return true;
    }

    private void nextStageInstance(Player player) {
        Stage stage = gameManagerService.nextStage();
        PlayerMessage.success("Changed stage of current instance to " + stage.toString(), player);
    }

    private void removePlayer(Player player, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if(target == null) {
            throw new IllegalArgumentException("Player " + playerName + " not found");
        }
        gameManagerService.deletePlayer(target);
        PlayerMessage.success("Removed player " + target.getName() + " from current instance", player);
    }

    private void addPlayer(Player player, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if(target == null) {
            throw new IllegalArgumentException("Player " + playerName + " not found");
        }
        gameManagerService.addPlayer(target);
        PlayerMessage.success("Added player " + playerName + " to current instance", player);
    }

    private void stopInstance(Player player) {
        PlayerMessage.info("Stopping current instance", player);
        gameManagerService.end();
    }

    private void createInstance(Player player, String lobbyName) {
        Lobby lobby = lobbyService.getLobbySafe(lobbyName);
        GameInstance instance = gameManagerService.createInstance(lobby);
        PlayerMessage.success("Created new game instance with id: " + instance.getIdentifier(), player);
    }

    private void infoInstance(Player player) {
        GameInstance gameInstance = gameManagerService.getCurrentInstance();
        var removedPlayers = gameInstance.getRemovedPlayers().keySet().stream().map(Player::getName).distinct().toList();
        var livingPlayers = gameInstance.getActivePlayers().keySet().stream().map(Player::getName).distinct().toList();
        var allPlayers = gameInstance.getAllPlayers().stream().map(Player::getName).distinct().toList();

        PlayerMessage.success("Displaying info for %s".formatted(gameInstance.getIdentifier()), player);
        PlayerMessage.info("Current Stage: %s".formatted(gameInstance.getCurrentStage().getName()), player);
        PlayerMessage.info("Game Result: %s".formatted(gameInstance.getGameResult()), player);
        PlayerMessage.info("Lobby: %s".formatted(gameInstance.getLobby().getLobbyName()), player);
        if(gameInstance.getArena() != null) {
            PlayerMessage.info("Arena: %s".formatted(gameInstance.getArena().getArenaName()), player);
        } else {
            PlayerMessage.info("Arena: Arena not set", player);
        }
        PlayerMessage.info("Dead players (%s): %s".formatted(removedPlayers.size(), removedPlayers.toString()), player);
        PlayerMessage.info("Living players (%s): %s".formatted(livingPlayers.size(), livingPlayers.toString()), player);
        PlayerMessage.info("Registered players (%s): %s".formatted(allPlayers.size(), allPlayers.toString()), player);
        PlayerMessage.info("", player);
    }

    private void listArchivedInstances(Player player) {
        Collection<ArchivedGame> archivedGames = gameManagerService.listArchived();
        PlayerMessage.info("Listing %s Archived instances".formatted(archivedGames.size()), player);
        archivedGames.forEach(archivedGame -> {
                    String message = "Instance: %s (%s) - Arena: %s - Lobby %s - Players: %s".formatted(
                            archivedGame.getInstanceId(),
                            archivedGame.getStage().name(),
                            archivedGame.getArenaName(),
                            archivedGame.getLobbyName(),
                            archivedGame.getPlayerNames());
                    PlayerMessage.info(message, player);
                }
        );
    }

    private void startInstance(String arenaName) {
        Arena arena = arenaService.getArenaSafe(arenaName);
        gameManagerService.start(arena);
        PlayerMessage.success("Successfully started current instance in arena: %s".formatted(arenaName));
    }

    private void showHelp(Player player) {
        player.sendMessage(GOLD + "GameManager Commands:");
        player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
        player.sendMessage(GOLD+ "/gm help " + ChatColor.GRAY + ": " + YELLOW + " Show this help");
        player.sendMessage(GOLD+ "/gm list " + ChatColor.GRAY + ": " + YELLOW + " List all archived instances");
        player.sendMessage(GOLD+ "/gm info " + ChatColor.GRAY + ": " + YELLOW + " Display the current instance details");
        player.sendMessage(GOLD+ "/gm nextstage " + ChatColor.GRAY + ": " + YELLOW + " Trigger the next stage of the current instance");
        player.sendMessage(GOLD+ "/gm create <lobby> " + ChatColor.GRAY + ": " + YELLOW + " Create a new instance from given lobby");
        player.sendMessage(GOLD+ "/gm start <arena> " + ChatColor.GRAY + ": " + YELLOW + " Start current instance with given arena");
        player.sendMessage(GOLD+ "/gm addplayer <player> " + ChatColor.GRAY + ": " + YELLOW + " Add a player to the current instance");
        player.sendMessage(GOLD+ "/gm removeplayer <player> " + ChatColor.GRAY + ": " + YELLOW + " Remove a player from the current instance");
        player.sendMessage(GOLD+ "/gm stop  " + ChatColor.GRAY + ": " + YELLOW + " Stop an instance");
        player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
    }

    @Override
    protected boolean consoleCommand(String[] args) {
        // TODO KapitanFloww implement console command logic
        return false;
    }
}
