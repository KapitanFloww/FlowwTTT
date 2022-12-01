package de.flowwindustries.flowwttt.game.listener;

import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Listen to the food-level-change of a player.
 */
@Log
@RequiredArgsConstructor
public class FoodLevelChangeListener implements Listener {

    private final GameManagerService gameManagerService;

    /**
     * Block food-level-change for players in an instance.
     * @param event - the event to handle
     */
    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof  Player player)) {
            log.fine("Food-Level change entity not a player");
            return;
        }

        GameInstance instance = gameManagerService.getInstanceOf(player);
        if(instance == null) {
            log.fine("Player %s is not in a valid game instance".formatted(player.getName()));
            return;
        }

        // Disable starvation
        player.setFoodLevel(20);
        event.setCancelled(true);
    }
}
