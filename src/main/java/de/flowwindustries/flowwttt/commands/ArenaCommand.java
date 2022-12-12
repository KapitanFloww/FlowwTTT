package de.flowwindustries.flowwttt.commands;

import de.flowwindustries.flowwttt.domain.items.ChestType;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.ChestSpawn;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import de.flowwindustries.flowwttt.services.ArenaService;
import lombok.extern.java.Log;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

import static de.flowwindustries.flowwttt.TTTPlugin.COORDINATE_FORMATTER;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.YELLOW;

/**
 * Command class for {@code /arena}
 */
@Log
public class ArenaCommand extends AbstractCommand {

    public static final String UNKNOWN_ARG_0 = "Unknown argument on index 0";
    private final ArenaService arenaService;

    public ArenaCommand(String permission, ArenaService arenaService) {
        super(permission);
        this.arenaService = arenaService;
    }

    @Override
    protected boolean playerCommand(Player player, String[] args) {
        switch (args.length) {
            case 1 -> {
                if(args[0].equalsIgnoreCase("list")) {
                    listArenas(player, false);
                }
                else if(args[0].equalsIgnoreCase("help")) {
                    showHelp(player);
                }
            }
            case 2 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "create" -> createArena(player, args[1]);
                    case "delete" -> deleteArena(player, args[1]);
                    case "info" -> infoArena(player, args[1]);
                    case "addspawn" -> addSpawn(player, args[1]);
                    case "addchest" -> addChest(player, args[1], ChestType.DEFAULT);
                    case "addlegendary" -> addChest(player, args[1], ChestType.LEGENDARY);
                    case "list" -> {
                        if(args[1].equalsIgnoreCase("full")) {
                            listArenas(player, true);
                        }
                    }
                    default -> throw new IllegalArgumentException(UNKNOWN_ARG_0);
                }
                return true;
            }
            case 3 -> {
                try {
                    int id = Integer.parseInt(args[2]);
                    switch (args[0].toLowerCase(Locale.ROOT)) {
                        case "removespawn" -> removeSpawn(player, args[1], id); // /arena removespawn <name> <id>
                        case "removechest" -> removeChest(player, args[1], id); // /arena removespawn <name> <id>
                        default -> throw new IllegalArgumentException(UNKNOWN_ARG_0);
                    }
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Not a number: " + args[2]);
                }
            }
            default -> throw new IllegalArgumentException("See /arena help");
        }
        return true;
    }

    @Override
    protected boolean consoleCommand(String[] args) {
        log.info("Not yet implemented!");
        return false;
    }

    private void createArena(Player player, String arenaName) {
        arenaService.createArena(arenaName);
        PlayerMessage.success("Created arena: " + arenaName, player);
    }

    private void deleteArena(Player player, String arenaName) {
        arenaService.deleteArena(arenaName);
        PlayerMessage.success("Deleted arena: " + arenaName, player);
    }

    private void infoArena(Player player, String arenaName) {
        PlayerMessage.info("Details of arena: " + arenaName, player);
        printArena(player, true, arenaService.getArenaSafe(arenaName));
    }

    private void addSpawn(Player player, String arenaName) {
        PlayerSpawn spawn = new PlayerSpawn();
        spawn.setX(player.getLocation().getX());
        spawn.setY(player.getLocation().getY());
        spawn.setZ(player.getLocation().getZ());
        spawn.setYaw(player.getLocation().getYaw());
        spawn.setPitch(player.getLocation().getPitch());
        spawn.setWorldName(player.getWorld().getName());

        arenaService.addPlayerSpawn(arenaName, spawn);
        PlayerMessage.success(String.format("Added spawn to arena %s", arenaName), player);
    }

    private void addChest(Player player, String arenaName, ChestType chestType) {
        ChestSpawn spawn = new ChestSpawn();
        spawn.setX(player.getLocation().getX());
        spawn.setY(player.getLocation().getY());
        spawn.setZ(player.getLocation().getZ());
        spawn.setWorldName(player.getWorld().getName());
        spawn.setType(chestType);

        arenaService.addChestSpawn(arenaName, spawn);
        PlayerMessage.success(String.format("Added chest (%s) to arena %s", chestType, arenaName), player);
    }

    private void removeSpawn(Player player, String arenaName, int id) {
        arenaService.clearPlayerSpawn(arenaName, id);
        PlayerMessage.success(String.format("Removed spawn: %s of arena %s", id, arenaName), player);
    }

    private void removeChest(Player player, String arenaName, int id) {
        arenaService.clearChestSpawn(arenaName, id);
        PlayerMessage.success(String.format("Removed chest: %s of arena %s", id, arenaName), player);
    }

    private void listArenas(Player player, boolean fullList) {
        PlayerMessage.info("Arenas:", player);
        arenaService.getAll().forEach(arena -> printArena(player, fullList, arena));
    }

    private void printArena(Player player, boolean fullList, Arena arena) {
        player.sendMessage(GOLD + arena.getArenaName() + YELLOW + " (" + GOLD + arena.getPlayerSpawns().size() + " Players" + YELLOW + ", " + GOLD + arena.getChestSpawns().size() + " Chests" + YELLOW + ")");
        if(fullList) {
            arena.getPlayerSpawns().forEach(playerSpawn ->
                    player.sendMessage(String.format(GOLD + "Spawn " + YELLOW + "[ID: %s]: %s: %s, %s, (%s)",
                            playerSpawn.getId(),
                            COORDINATE_FORMATTER.format(playerSpawn.getX()),
                            COORDINATE_FORMATTER.format(playerSpawn.getY()),
                            COORDINATE_FORMATTER.format(playerSpawn.getZ()),
                            playerSpawn.getWorldName()
                    )));
            arena.getChestSpawns().forEach(chestSpawn ->
                    player.sendMessage(String.format(GOLD + "Chest " + YELLOW + "[ID: %s]: %s: %s, %s, (%s) (%s)",
                            chestSpawn.getId(),
                            COORDINATE_FORMATTER.format(chestSpawn.getX()),
                            COORDINATE_FORMATTER.format(chestSpawn.getY()),
                            COORDINATE_FORMATTER.format(chestSpawn.getZ()),
                            chestSpawn.getWorldName(),
                            chestSpawn.getType()
                    )));
        }
    }

    private void showHelp(Player player) {
        player.sendMessage(GOLD + "Arena Commands:");
        player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
        player.sendMessage(GOLD+ "/arena help " + ChatColor.GRAY + ": " + YELLOW + " Show this help");
        player.sendMessage(GOLD+ "/arena list " + ChatColor.GRAY + ": " + YELLOW + " Display all arenas");
        player.sendMessage(GOLD+ "/arena list full" + ChatColor.GRAY + ": " + YELLOW + " Display all arenas with extended details");
        player.sendMessage(GOLD+ "/arena info <name>" + ChatColor.GRAY + ": " + YELLOW + " Display extended information of this arena");
        player.sendMessage(GOLD+ "/arena create <name>" + ChatColor.GRAY + ": " + YELLOW + " Create a new arena with that name");
        player.sendMessage(GOLD+ "/arena delete <name>" + ChatColor.GRAY + ": " + YELLOW + " Delete the arena with that name");
        player.sendMessage(GOLD+ "/arena addspawn <name>" + ChatColor.GRAY + ": " + YELLOW + " Add a player spawn point to this arena");
        player.sendMessage(GOLD+ "/arena addchest <name>" + ChatColor.GRAY + ": " + YELLOW + " Add a chest spawn point to this arena");
        player.sendMessage(GOLD+ "/arena addlegendary <name>" + ChatColor.GRAY + ": " + YELLOW + " Add a legendary chest spawn point to this arena");
        player.sendMessage(GOLD+ "/arena removespawn <name> <id>" + ChatColor.GRAY + ": " + YELLOW + " Remove the spawn point with the given id from the arena");
        player.sendMessage(GOLD+ "/arena removechest <name> <id>" + ChatColor.GRAY + ": " + YELLOW + " Remove the chest with the given id from the arena");
        player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
    }
}