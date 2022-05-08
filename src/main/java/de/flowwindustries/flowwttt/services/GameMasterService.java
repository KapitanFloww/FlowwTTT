package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.GameInstance;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;

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
     * Start the given game instance in the given arena.
     * @param instance the instance to start
     * @param arena the arena to play in
     */
    void start(GameInstance instance, Arena arena);

    /**
     * Go to the next stage of the match.
     * @param instance the instance to change the stage
     */
    void nextStage(GameInstance instance);

    /**
     * End a game instance (the normal way).
     * @param instance the instance to end
     */
    void end(GameInstance instance);

    /**
     * Forcefully stop a game instance.
     * @param instance the instance to stop
     */
    void stop(GameInstance instance);
}
