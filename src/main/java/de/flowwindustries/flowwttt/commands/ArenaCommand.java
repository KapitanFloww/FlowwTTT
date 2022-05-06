package de.flowwindustries.flowwttt.commands;

import de.flowwindustries.flowwttt.domain.locations.ChestSpawn;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import de.flowwindustries.flowwttt.exceptions.InvalidArgumentException;
import de.flowwindustries.flowwttt.services.ArenaService;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Command class for {@code /arena}
 */
@Log
public class ArenaCommand extends AbstractCommand {

    private final ArenaService arenaService;

    public ArenaCommand(String permission, ArenaService arenaService) {
        super(permission);
        this.arenaService = arenaService;
    }

    @Override
    protected boolean playerCommand(Player player, String[] args) throws InvalidArgumentException {
        switch (args.length) {
            case 1 -> {
                if(args[0].equalsIgnoreCase("list")) {
                    listArenas(player, false);
                }
            }
            case 2 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "create" -> createArena(player, args[1]);
                    case "delete" -> deleteArena(player, args[1]);
                    case "addspawn" -> addSpawn(player, args[1]);
                    case "addchest" -> addChest(player, args[1]);
                    case "list" -> {
                        if(args[1].equalsIgnoreCase("full")) {
                            listArenas(player, true);
                        }
                    }
                    default -> throw new InvalidArgumentException(player, "Unknown argument on index 0");
                }
                return true;
            }
            case 3 -> {
                try {
                    int id = Integer.parseInt(args[2]);
                    switch (args[0].toLowerCase(Locale.ROOT)) {
                        case "removespawn" -> removeSpawn(player, args[1], id); // /arena removespawn <name> <id>
                        case "removechest" -> removeChest(player, args[1], id); // /arena removespawn <name> <id>
                        default -> throw new InvalidArgumentException(player, "Unknown argument on index 0");
                    }
                } catch (NumberFormatException ex) {
                    throw new InvalidArgumentException(player, "Not a number: " + args[2]);
                }
            }
            default -> throw new InvalidArgumentException(player, "See /arena help");
        }
        return true;
    }

    private void listArenas(Player player, boolean fullList) {
        PlayerMessage.info("Arenas:", player);
        player.sendMessage("");
        arenaService.getAll().forEach(arena -> {
            DecimalFormat formatter = new DecimalFormat("#.##");
            PlayerMessage.info(arena.getName() + " (" + arena.getPlayerSpawns().size() + " Players, " + arena.getChestSpawns().size() + " Chests)", player);
            if(fullList) {
                arena.getPlayerSpawns().forEach(playerSpawn ->
                        PlayerMessage.info(String.format("Spawn [%s]: %s: %s, %s, (%s)",
                                playerSpawn.getId(),
                                formatter.format(playerSpawn.getX()),
                                formatter.format(playerSpawn.getY()),
                                formatter.format(playerSpawn.getZ()),
                                playerSpawn.getWorldName()
                        ), player));
                arena.getChestSpawns().forEach(chestSpawn ->
                        PlayerMessage.info(String.format("Chest [%s]: %s: %s, %s, (%s)",
                                chestSpawn.getId(),
                                formatter.format(chestSpawn.getX()),
                                formatter.format(chestSpawn.getY()),
                                formatter.format(chestSpawn.getZ()),
                                chestSpawn.getWorldName()
                        ), player));
            }
        });
    }

    @Override
    protected boolean consoleCommand(String[] args) throws InvalidArgumentException {
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

    private void addChest(Player player, String arenaName) {
        ChestSpawn spawn = new ChestSpawn();
        spawn.setX(player.getLocation().getX());
        spawn.setY(player.getLocation().getY());
        spawn.setZ(player.getLocation().getZ());
        spawn.setWorldName(player.getWorld().getName());

        arenaService.addChestSpawn(arenaName, spawn);
        PlayerMessage.success(String.format("Added chest to arena %s", arenaName), player);
    }

    private void removeSpawn(Player player, String arenaName, int id) {
        arenaService.clearPlayerSpawn(arenaName, id);
        PlayerMessage.success(String.format("Removed spawn: %s of arena %s", id, arenaName), player);
    }

    private void removeChest(Player player, String arenaName, int id) {
        arenaService.clearChestSpawn(arenaName, id);
        PlayerMessage.success(String.format("Removed chest: %s of arena %s", id, arenaName), player);
    }
}
