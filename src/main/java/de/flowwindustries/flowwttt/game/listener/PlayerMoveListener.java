package de.flowwindustries.flowwttt.game.listener;

import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class PlayerMoveListener implements Listener {

    private final GameManagerService gameManagerService;

    /**
     * Block movement of players when they are in a valid game instance and the stage is {@link Stage#COUNTDOWN}.
     * @param event the event to handle
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        GameInstance instance = gameManagerService.getInstanceOf(event.getPlayer());
        if(instance == null) {
            return;
        }
        if(instance.getCurrentStage().getName() != Stage.COUNTDOWN) {
            return;
        }
        event.setCancelled(true);
    }
}
