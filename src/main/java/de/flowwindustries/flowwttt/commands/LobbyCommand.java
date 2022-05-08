package de.flowwindustries.flowwttt.commands;

import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import de.flowwindustries.flowwttt.exceptions.InvalidArgumentException;
import de.flowwindustries.flowwttt.services.LobbyService;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;

import static de.flowwindustries.flowwttt.TTTPlugin.COORDINATE_FORMATTER;
import static de.flowwindustries.flowwttt.commands.ArenaCommand.UNKNOWN_ARG_0;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.YELLOW;

/**
 * Command class for {@code /lobby}.
 */
@Log
public class LobbyCommand extends AbstractCommand {

    private final LobbyService lobbyService;

    public LobbyCommand(String permission, LobbyService lobbyService) {
        super(permission);
        this.lobbyService = lobbyService;
    }

    @Override
    protected boolean playerCommand(Player player, String[] args) throws InvalidArgumentException {
        switch (args.length) {
            case 0 -> teleportDefault(player);
            case 1 -> {
                if(args[0].equalsIgnoreCase("help")) {
                    showHelp(player);
                    return true;
                }
                if(args[0].equalsIgnoreCase("list")) {
                    listLobby(player);
                    return true;
                }
                teleportLobby(player, args[0]);
            }
            case 2 -> {
                switch (args[0]) {
                    case "create" -> createLobby(player, args[1]);
                    case "delete" -> deleteLobby(player, args[1]);
                    case "setspawn" -> setSpawnLobby(player, args[1]);
                    case "setdefault" -> setDefaultLobby(player, args[1]);
                    default -> throw new InvalidArgumentException(player, UNKNOWN_ARG_0);
                }
            }
            case 3 -> {
                if(args[0].equalsIgnoreCase("addarena")) {
                    addLobbyArena(player, args[1], args[2]);
                    return true;
                }
                if(args[0].equalsIgnoreCase("removearena")) {
                    removeLobbyArena(player, args[1], args[2]);
                    return true;
                }
                throw new InvalidArgumentException(player, UNKNOWN_ARG_0);
            }
            default -> throw new InvalidArgumentException(player, "See /lobby help");
        }
        return true;
    }

    private void teleportDefault(Player player) {
        String defaultLobby = lobbyService.getDefaultLobbyName();
        teleportLobby(player, defaultLobby);
    }

    private void setDefaultLobby(Player player, String lobbyName) {
        lobbyService.setDefaultLobby(lobbyName);
        PlayerMessage.success("Set default lobby to: " + lobbyName, player);
    }

    private void listLobby(Player player) {
        PlayerMessage.info("Lobby-List:", player);
        lobbyService.getAll().forEach(lobby -> printLobby(player, lobby));
    }

    private void printLobby(Player player, Lobby lobby) {
        player.sendMessage(String.format(GOLD + "[%s]: [%s, %s, %s, (%s)], Arenas: %s",
                lobby.getLobbyName(),
                COORDINATE_FORMATTER.format(lobby.getLobbySpawn().getX()),
                COORDINATE_FORMATTER.format(lobby.getLobbySpawn().getY()),
                COORDINATE_FORMATTER.format(lobby.getLobbySpawn().getY()),
                lobby.getLobbySpawn().getWorldName(),
                lobby.getArenas().size()
                ));
    }

    @Override
    protected boolean consoleCommand(String[] args) throws InvalidArgumentException {
        log.info("Not yet implemented!");
        return false;
    }

    private void createLobby(Player player, String lobbyName) {
        lobbyService.createLobby(lobbyName);
        PlayerMessage.success("Created lobby: " + lobbyName, player);
    }

    private void setSpawnLobby(Player player, String lobbyName) {
        Location playerLocation = player.getLocation();
        PlayerSpawn spawn = new PlayerSpawn();
        spawn.setX(playerLocation.getX());
        spawn.setY(playerLocation.getY());
        spawn.setZ(playerLocation.getZ());
        spawn.setYaw(playerLocation.getYaw());
        spawn.setPitch(playerLocation.getPitch());
        spawn.setWorldName(playerLocation.getWorld().getName());
        lobbyService.setLobbySpawn(lobbyName, spawn);
        PlayerMessage.success("Successfully set lobby spawn for lobby: " + lobbyName, player);
    }

    private void deleteLobby(Player player, String lobbyName) {
        lobbyService.deleteLobby(lobbyName);
        PlayerMessage.success("Removed lobby: " + lobbyName, player);
    }

    private void teleportLobby(Player player, String lobbyName) {
        PlayerSpawn lobbySpawn = lobbyService.getLobbySpawn(lobbyName);
        //TODO #3 Check if player is in game instance
        //TODO #3 Remove player form game instance
        World world = getWorldSafe(lobbySpawn.getWorldName());
        Location location = new Location(world, lobbySpawn.getX(), lobbySpawn.getY(), lobbySpawn.getZ(), lobbySpawn.getYaw(), lobbySpawn.getPitch());
        player.teleport(location);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        PlayerMessage.success("Teleported back to lobby: " + lobbyName, player);
    }


    private void removeLobbyArena(Player player, String lobbyName, String arenaName) {
        lobbyService.removeArena(lobbyName, arenaName);
        PlayerMessage.success("Removed arena: " + arenaName + " from lobby: " + lobbyName, player);
    }

    private void addLobbyArena(Player player, String lobbyName, String arenaName) {
        lobbyService.addArena(lobbyName, arenaName);
        PlayerMessage.success("Added arena: " + arenaName + " to lobby: " + lobbyName, player);
    }

    private void showHelp(Player player) {
        player.sendMessage(GOLD + "Lobby Commands:");
        player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
        player.sendMessage(GOLD+ "/lobby " + ChatColor.GRAY + ": " + YELLOW + " Teleport to default lobby");
        player.sendMessage(GOLD+ "/lobby <name> " + ChatColor.GRAY + ": " + YELLOW + " Teleport to specific lobby");
        player.sendMessage(GOLD+ "/lobby help " + ChatColor.GRAY + ": " + YELLOW + " Show this help");
        player.sendMessage(GOLD+ "/lobby list " + ChatColor.GRAY + ": " + YELLOW + " Display all lobbies");
        player.sendMessage(GOLD+ "/lobby arenas <name>" + ChatColor.GRAY + ": " + YELLOW + " Display the arenas of this lobby");
        player.sendMessage(GOLD+ "/lobby create <name>" + ChatColor.GRAY + ": " + YELLOW + " Create a new lobby");
        player.sendMessage(GOLD+ "/lobby delete <name>" + ChatColor.GRAY + ": " + YELLOW + " Delete the lobby");
        player.sendMessage(GOLD+ "/lobby setspawn <name>" + ChatColor.GRAY + ": " + YELLOW + " Set the lobby spawn point");
        player.sendMessage(GOLD+ "/lobby setdefault <name>" + ChatColor.GRAY + ": " + YELLOW + " Set the default lobby");
        player.sendMessage(GOLD+ "/lobby addarena <name> <arena>" + ChatColor.GRAY + ": " + YELLOW + " Add arena to lobby");
        player.sendMessage(GOLD+ "/lobby removearena <name> <arena>" + ChatColor.GRAY + ": " + YELLOW + " Remove arena from lobby");
        player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
    }

    private World getWorldSafe(String name) {
        return Optional.ofNullable(Bukkit.getWorld(name)).orElseThrow(() -> new IllegalArgumentException("World does not exist: " + name));
    }
}
