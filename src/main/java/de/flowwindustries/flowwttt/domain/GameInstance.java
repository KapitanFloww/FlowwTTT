package de.flowwindustries.flowwttt.domain;

import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import lombok.Data;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A game instance.
 * I.e: a match
 */
@Log
@Data
public class GameInstance {

    /**
     * This game's identifier.
     */
    private String identifier;

    /**
     * The current stage this game is in.
     */
    private Stage stage = Stage.LOBBY;

    /**
     * The arena this game takes place.
     */
    private Arena arena;

    /**
     * Map of players and their role.
     */
    private Map<Player, Role> playerRoles;

    /**
     * Current players.
     */
    private List<Player> players = new ArrayList<>();

    /**
     * Initialize the roles for the current players.
     */
    public void initializeRoles() {
        int playerSize = players.size();
        log.config("Assigning roles for " + playerSize + " players");

        // TODO: Assign roles
        // 10% Detective
        // 60% Innocent
        // 30% Traitor
    }

    /**
     * Add a player to this game instance.
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        players.add(player);
        log.config("Added player " + player.getName() + " to game instance " + this.getIdentifier());
    }

    /**
     * Remove a player from this game instance.
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        players.remove(player);
        log.config("Removed player " + player.getName() + " to game instance " +  this.getIdentifier());
    }

    /**
     * Increment this instance's stage to the next one.
     */
    public void nextStage() {
        switch (this.getStage()) {
            case LOBBY -> {
                log.config("Finished Lobby phase");
            }
            case GRACE_PERIOD -> {
                log.config("Finished Grace-Period phase");
            }
            case RUNNING -> {
                log.config("Finished Running phase");
            }
            case ENDGAME -> {
                log.config("Finished endgame");
            }
        }
    }
}
