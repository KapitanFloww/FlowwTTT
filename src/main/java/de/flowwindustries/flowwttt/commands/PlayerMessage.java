package de.flowwindustries.flowwttt.commands;

import de.flowwindustries.flowwttt.TTTPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utility class to simplify sending of colored messages to a player.
 */
public class PlayerMessage {

    /**
     * Send an {@code INFO} ({@link ChatColor#YELLOW}) message to a player.
     * @param message the message to send
     * @param players the players to send the message to
     */
    public static void info(String message, Player... players) {
        sendMessageIntern(ChatColor.YELLOW, message, List.of(players));
    }

    /**
     * Send an {@code INFO} ({@link ChatColor#YELLOW}) message to a player.
     * @param message the message to send
     * @param players the players to send the message to
     */
    public static void info(String message, List<Player> players) {
        sendMessageIntern(ChatColor.YELLOW, message, players);
    }

    /**
     * Send an {@code INFO} ({@link ChatColor#YELLOW}) message to a player.
     * @param message the message to send
     * @param players the players to send the message to
     */
    public static void info(String message, Collection<Player> players) {
        sendMessageIntern(ChatColor.YELLOW, message, players.stream().toList());
    }

    /**
     * Send an {@code SUCCESS} ({@link ChatColor#GREEN}) message to a player.
     * @param message the message to send
     * @param players the players to send the message to
     */
    public static void success(String message, Player... players) {
        sendMessageIntern(ChatColor.GREEN, message, List.of(players));
    }

    /**
     * Send an {@code WARN} ({@link ChatColor#RED}) message to a player.
     * @param message the message to send
     * @param players the players to send the message to
     */
    public static void warn(String message, Player... players) {
        sendMessageIntern(ChatColor.RED, message, List.of(players));
    }

    /**
     * Send an {@code ERROR} ({@link ChatColor#DARK_RED}) message to a player.
     * @param message the message to send
     * @param players the players to send the message to
     */
    public static void error(String message, Player... players) {
        sendMessageIntern(ChatColor.DARK_RED, message, List.of(players));
    }

    private static void sendMessageIntern(ChatColor chatColor, String message, List<Player> players) {
        players.forEach(player ->
                player.sendMessage(String.format("%s[%s%s%s] %s%s",
                        ChatColor.GRAY,
                        ChatColor.DARK_RED,
                        TTTPlugin.getInstance().getDescription().getName(),
                        ChatColor.GRAY,
                        chatColor, message)));
    }
}
