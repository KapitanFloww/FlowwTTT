package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.GameInstance;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service interface to manage all running game instances.
 */
public interface GameManagerService {

    /**
     * Instances-TaskId map.
     * Used to cancel running tasks (i.e. idlers, countdowns) forcefully.
     */
    Map<String, List<Integer>> instancesTaskMap = new ConcurrentHashMap<>();

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

    /**
     * List all archived instances.
     * @return all archived instances.
     */
    Collection<ArchivedGame> listArchived();

    /**
     * Add a running taskId.
     * @param instanceId associated instance id
     * @param taskId the task id
     */
    static void addInstanceTaskId(String instanceId, Integer taskId) {
        if(!instancesTaskMap.containsKey(instanceId)) {
            instancesTaskMap.put(instanceId, new ArrayList<>());
        }
        List<Integer> taskIds = instancesTaskMap.get(instanceId);
        taskIds.add(taskId);
        instancesTaskMap.put(instanceId, taskIds);
    }

    /**
     * Remove a taskId.
     * @param instanceId associated instance id
     * @param taskId the task id
     */
    static void removeInstanceTaskId(String instanceId, Integer taskId) {
        if(!instancesTaskMap.containsKey(instanceId)) {
            return;
        }
        List<Integer> taskIds = instancesTaskMap.get(instanceId);
        taskIds.remove(taskId);
        instancesTaskMap.put(instanceId, taskIds);
    }

    /**
     * Get instances task ids.
     * @param instanceId the instance to get the task ids of
     * @return a list of task ids or {@code null} of no task ids are found for the given instance
     */
    static List<Integer> getInstanceTask(String instanceId) {
        return instancesTaskMap.get(instanceId);
    }

    /**
     * Clear the tasks of this instance.
     * @param instanceId the instance id to clear the task ids of
     */
    static void clearTasks(String instanceId) {
        instancesTaskMap.remove(instanceId);
    }
}
