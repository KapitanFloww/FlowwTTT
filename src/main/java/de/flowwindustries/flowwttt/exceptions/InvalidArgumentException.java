package de.flowwindustries.flowwttt.exceptions;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Exception to be thrown, if the arguments a player passed are not valid.
 */
public class InvalidArgumentException extends Exception {

    @Getter
    private final CommandSender sender;
    @Getter
    private final String reason;

    public InvalidArgumentException(Player sender, String reason) {
        this.sender = sender;
        this.reason = reason;
    }
}
