package de.flowwindustries.flowwttt.exceptions;

import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * Exception to be thrown if a player does not have the required permissions.
 */
public class InsufficientPermissionsException extends Exception {

    @Getter
    private final Player sender;
    @Getter
    private final String permission;

    public InsufficientPermissionsException(Player sender, String permission) {
        super();
        this.sender = sender;
        this.permission = permission;
    }
}
