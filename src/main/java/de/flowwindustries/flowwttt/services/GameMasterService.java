package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.GameInstance;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import org.bukkit.entity.Player;

/**
 * Service interface to manage all running game instances.
 */
public interface GameMasterService {

    /**
     * Create a new game instance for the following lobby.
     * @param lobby the lobby the players start and return to
     * @return the create instance
     */
    GameInstance newInstance(Lobby lobby);

    /**
     * Get the instance of this identifier.
     * @param identifier the identifier of the requested game instance
     * @return the requested game instance
     */
    GameInstance getGameInstance(String identifier);

    /**
     * Get a players instance.
     * @param player the player to get the instance from.
     * @return the game instance
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
     */
    void nextStage(String identifier);

    /**
     * End a game instance (the normal way).
     * @param identifier of the instance to end
     */
    void end(String identifier);

    /**
     * Forcefully stop a game instance.
     * @param identifier of the instance to stop
     */
    void stop(String identifier);
}
