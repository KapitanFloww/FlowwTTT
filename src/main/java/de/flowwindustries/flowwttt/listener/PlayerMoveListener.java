package de.flowwindustries.flowwttt.listener;

import de.flowwindustries.flowwttt.domain.GameInstance;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.services.GameMasterService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class PlayerMoveListener implements Listener {

    private final GameMasterService gameMasterService;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        String instanceId = gameMasterService.getPlayerInstanceMap().get(event.getPlayer());
        if(instanceId == null) {
            return;
        }
        GameInstance instance = gameMasterService.getGameInstance(instanceId);
        if(instance.getStage() == Stage.COUNTDOWN) {
            event.setCancelled(true);
        }
    }
}
