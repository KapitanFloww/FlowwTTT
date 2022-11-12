package de.flowwindustries.flowwttt.listener;

import de.flowwindustries.flowwttt.GameInstance;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class PlayerDeathListener {

    /**
     * Block movement of players when they are in a valid game instance and the stage is {@link Stage#COUNTDOWN}.
     * @param event the event to handle
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(enabled) {

        }
        GameInstance instance = gameManagerService.getInstanceOf(event.getPlayer());
        if(instance == null) {
            return;
        }
        if(instance.getStage() != Stage.COUNTDOWN) {
            return;
        }
        event.setCancelled(true);
    }
}
