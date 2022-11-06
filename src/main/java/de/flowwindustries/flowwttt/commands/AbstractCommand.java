package de.flowwindustries.flowwttt.commands;

import de.flowwindustries.flowwttt.exceptions.InsufficientPermissionsException;
import lombok.extern.java.Log;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Log
public abstract class AbstractCommand implements CommandExecutor {

    public static final String INVALID_ARGUMENTS = "Invalid argument: %s";
    public static final String NO_PERMISSION = "You do not have to permissions to execute this command";
    public static final String NO_PERMISSION_LOG = "Player %s tried to access a command without permission: %s";

    private final String permission;

    public AbstractCommand(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //If sender is player
        if (sender instanceof Player player) {
            try {
                if (!player.hasPermission(permission)) {
                    throw new InsufficientPermissionsException(player, permission);
                }
                return playerCommand(player, args);
            } catch (InsufficientPermissionsException ex) {
                PlayerMessage.error(NO_PERMISSION, player);
                log.warning(String.format(NO_PERMISSION_LOG, ex.getSender().getName(), ex.getPermission()));
                return true;
            } catch (IllegalArgumentException ex) {
                PlayerMessage.warn(String.format(INVALID_ARGUMENTS, ex.getMessage()), player);
                return false;
            }
        }
        //If sender is not player
        try {
            return consoleCommand(args);
        } catch (IllegalArgumentException ex) {
            log.warning(String.format(INVALID_ARGUMENTS, ex.getMessage()));
            return false;
        }
    }

    /**
     * Abstract template for player commands.
     *
     * @param player the executing player
     * @param args command arguments
     * @return  {@code true} if the command executed successful, otherwise {@code false}
     */
    protected abstract boolean playerCommand(Player player, String[] args);

    /**
     * Abstract template for console commands.
     * @param args command arguments
     * @return {@code true} if the command executed successful, otherwise {@code false}
     */
    protected abstract boolean consoleCommand(String[] args);
}
