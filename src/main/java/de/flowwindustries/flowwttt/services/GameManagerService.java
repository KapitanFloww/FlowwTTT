package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.game.GameInstance;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Service interface to manage all running game instances.
 */
public interface GameManagerService {

    /**
     * Create a new game instance for the following lobby.
     * @param lobby the lobby the players start and return to
     * @return the created instance
     */
    GameInstance createInstance(Lobby lobby);

    /**
     * Checks if the player is part of the current instance.
     * @param player the player to be checked
     * @return @{@code true} if the player is part of the current instance. Otherwise {@code false}
     */
    boolean isInCurrentInstance(Player player);

    /**
     * Get the current instance.
     * @return the current instance
     */
    GameInstance getCurrentInstance();

    /**
     * Start the current instance with the given arena.
     * @param arena the arena to start the instance with
     */
    void start(Arena arena);

    /**
     * Add a player to the current game instance.
     * @param player the player to be added
     */
    void addPlayer(Player player);

    /**
     * Remove a player from the current game instance.
     * @param player the player to be added
     */
    void deletePlayer(Player player);

    /**
     * Go to the next stage of the match.
     * @return the updated stage of this instance
     * @throws IllegalStateException if this instance's stage is invalid
     */
    Stage nextStage() throws IllegalStateException ;

    /**
     * End the current instance.
     */
    void end();

    /**
     * List all archived instances.
     * @return all archived instances.
     */
    Collection<ArchivedGame> listArchived();

    /**
     * Cleanup the current instance.
     */
    void cleanupInstance();
}
