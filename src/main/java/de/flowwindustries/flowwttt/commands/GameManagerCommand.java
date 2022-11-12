package de.flowwindustries.flowwttt.commands;

import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.GameInstance;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
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
                    case "list" -> listInstances(player);
                    case "archived" -> listArchivedInstances(player);
                    default -> throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, args[0]));
                }
            }
            case 2 -> {
                switch (args[0]) {
                    case "info" -> infoInstance(player, args[1]);
                    case "create" -> createInstance(player, args[1]);
                    case "stop" -> stopInstance(player, args[1]);
                    case "nextstage" -> nextStageInstance(player, args[1]);
                    default -> throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, args[0]));
                }
            }
            case 3 -> {

                switch (args[0]) {
                    case "start" -> startInstance(args[1], args[2]);
                    case "addplayer" -> addPlayer(player, args[1], args[2]);
                    case "removeplayer" -> removePlayer(player, args[1], args[2]);
                    default -> throw new IllegalArgumentException(String.format(INVALID_ARGUMENTS, args[0]));
                }
            }
            default -> throw new IllegalArgumentException("See /gm help");
        }
        return true;
    }

    private void listArchivedInstances(Player player) {
        Collection<ArchivedGame> games = gameManagerService.listArchived();
        PlayerMessage.info(String.format(GOLD + "Listing %s Instances:", games.size()), player);
        gameManagerService.listArchived().forEach(gameInstance -> {
                    player.sendMessage(String.format(YELLOW + "[%s]: %s, %s, Lobby: %s, Arena: %s, Timestamp: %s, Players: ",
                            gameInstance.getInstanceId(),
                            gameInstance.getGameResult(),
                            gameInstance.getStage(),
                            gameInstance.getLobbyName(),
                            gameInstance.getArenaName(),
                            gameInstance.getEndedAt(),
                            gameInstance.getPlayerNames()
                    ));
                }
        );
    }

    private void nextStageInstance(Player player, String instanceId) {
        Stage stage = gameManagerService.nextStage(instanceId);
        PlayerMessage.success("Changed stage of instance to " + stage.toString(), player);
    }

    private void removePlayer(Player player, String instanceId, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if(target == null) {
            throw new IllegalArgumentException("Player " + playerName + " not found");
        }
        gameManagerService.deletePlayer(instanceId, target);
        PlayerMessage.success("Removed player " + target.getName() + " from this instance", player);
    }

    private void addPlayer(Player player, String instanceId, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if(target == null) {
            throw new IllegalArgumentException("Player " + playerName + " not found");
        }
        gameManagerService.addPlayer(instanceId, target);
        PlayerMessage.success("Added player " + playerName + " to this instance", player);
    }

    private void stopInstance(Player player, String instanceId) {
        PlayerMessage.info("Stopping instance " + instanceId, player);
        gameManagerService.end(instanceId);
    }

    private void createInstance(Player player, String lobbyName) {
        Lobby lobby = lobbyService.getLobbySafe(lobbyName);
        GameInstance instance = gameManagerService.newInstance(lobby);
        PlayerMessage.success("Created new game instance with id: " + instance.getIdentifier(), player);
    }

    private void infoInstance(Player player, String instanceId) {
        GameInstance instance = gameManagerService.getGameInstanceSafe(instanceId);
        PlayerMessage.info(String.format(GOLD + "Instance %s Details:", instance.getIdentifier()), player);
        player.sendMessage(String.format(YELLOW + "Stage: %s", instance.getStage()));
        player.sendMessage(String.format(YELLOW + "Lobby: %s", getLobbyName(instance)));
        player.sendMessage(String.format(YELLOW + "Arena: %s", getArenaName(instance)));
        player.sendMessage(String.format(YELLOW + "Players: %s:", instance.getCurrentPlayers().size()));
        player.sendMessage(String.format(YELLOW + "Result: %s:", instance.getGameResult()));
        instance.getCurrentPlayers().forEach(playerInInstance -> player.sendMessage(YELLOW + playerInInstance.getName()));
    }

    private void listInstances(Player player) {
        Collection<GameInstance> instances = gameManagerService.list();
        PlayerMessage.info(String.format(GOLD + "Listing %s Instances:", instances.size()), player);
        instances.forEach(gameInstance -> {
            player.sendMessage(String.format(YELLOW + "[%s]: %s %s %s (%s)",
                            gameInstance.getIdentifier(),
                            gameInstance.getStage(),
                            getLobbyName(gameInstance),
                            getArenaName(gameInstance),
                            gameInstance.getCurrentPlayers().size())
            );
            }
        );
    }

    private void startInstance(String instanceId, String arenaName) {
        Arena arena = arenaService.getArenaSafe(arenaName);
        gameManagerService.start(instanceId, arena);
        PlayerMessage.success("Successfully started instance: " + instanceId);
    }

    private void showHelp(Player player) {
        player.sendMessage(GOLD + "GameManager Commands:");
        player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
        player.sendMessage(GOLD+ "/gm help " + ChatColor.GRAY + ": " + YELLOW + " Show this help");
        player.sendMessage(GOLD+ "/gm list " + ChatColor.GRAY + ": " + YELLOW + " List all instances");
        player.sendMessage(GOLD+ "/gm info <id> " + ChatColor.GRAY + ": " + YELLOW + " Display instance details");
        player.sendMessage(GOLD+ "/gm nextstage <id> " + ChatColor.GRAY + ": " + YELLOW + " Trigger the next stage of this instance");
        player.sendMessage(GOLD+ "/gm create <lobby> " + ChatColor.GRAY + ": " + YELLOW + " Create a new instance from lobby");
        player.sendMessage(GOLD+ "/gm start <id> <arena> " + ChatColor.GRAY + ": " + YELLOW + " Start instance with an arena");
        player.sendMessage(GOLD+ "/gm addplayer <id> <player> " + ChatColor.GRAY + ": " + YELLOW + " Add a player to this instance");
        player.sendMessage(GOLD+ "/gm removeplayer <id> <player> " + ChatColor.GRAY + ": " + YELLOW + " Remove a player from this instance");
        player.sendMessage(GOLD+ "/gm stop <id> " + ChatColor.GRAY + ": " + YELLOW + " Stop an instance");
        player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
    }

    @Override
    protected boolean consoleCommand(String[] args) {
        log.info("Not yet implemented!");
        return false;
    }

    private static String getLobbyName(GameInstance gameInstance) {
        if(gameInstance.getLobby() != null) {
            return gameInstance.getLobby().getLobbyName();
        }
        return "null";
    }

    private static String getArenaName(GameInstance gameInstance) {
        if(gameInstance.getArena() != null) {
            return gameInstance.getArena().getArenaName();
        }
        return "null";
    }
}
