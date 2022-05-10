package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.GameInstance;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Service interface to manage all running game instances.
 */
public interface GameManagerService {

    /**
     * Create a new game instance for the following lobby.
     * @param lobby the lobby the players start and return to
     * @return the create instance
     */
    GameInstance newInstance(Lobby lobby);

    /**
     * Null-safe way to get a game instance by its identifier.
     * @param identifier the identifier of the requested game instance
     * @return the requested game instance. Never null
     * @throws IllegalArgumentException if the instance is not found
     * @throws IllegalStateException if more than one instance is found
     */
    GameInstance getGameInstanceSafe(String identifier) throws IllegalArgumentException, IllegalStateException;

    /**
     * Get a players instance.
     * @param player the player to get the instance from.
     * @return the game instance of {@code null} if the player is not accoiated to an instance
     */
    GameInstance getInstanceOf(Player player);

    /**
     * Start the given game instance in the given arena.
     * @param identifier of the instance to start
     * @param arena the arena to play in
     */
    void start(String identifier, Arena arena);

    /**
     * Add a player to this game instance.
     * @param identifier of the instance to add the player to
     * @param player the player to be added
     */
    void addPlayer(String identifier, Player player);

    /**
     * Remove a player from this game instance.
     * @param identifier of the instance to be removed
     * @param player the player to be added
     */
    void deletePlayer(String identifier, Player player);

    /**
     * Go to the next stage of the match.
     * @param identifier of the instance to change
     * @return the updated stage of this instance
     * @throws IllegalStateException if this instance's stage is invalid
     */
    Stage nextStage(String identifier) throws IllegalStateException ;

    /**
     * End a game instance (the normal way).
     * @param identifier of the instance to end
     */
    void end(String identifier);

    /**
     * List all instances.
     * @return all instances
     */
    Collection<GameInstance> list();
}
