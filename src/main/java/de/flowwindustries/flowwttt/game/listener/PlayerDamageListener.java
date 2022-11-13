package de.flowwindustries.flowwttt.game.listener;

import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import static de.flowwindustries.flowwttt.utils.SpigotParser.mapSpawnToLocation;

/**
 * Listen to a player death.
 */
@RequiredArgsConstructor
public class PlayerDamageListener implements Listener {

    private final GameManagerService gameManagerService;

    /**
     * Block damage to players when they are in a valid game instance and the stage is not {@link Stage#RUNNING}.
     * @param event - the event to handle
     */
    @EventHandler
    public void onPlayerDamage(PlayerInteractEntityEvent event) {
        GameInstance instance = gameManagerService.getInstanceOf(event.getPlayer());
        if(instance == null) {
            return;
        }
        if(instance.getCurrentStage().getName() != Stage.RUNNING) {
            if(event.getRightClicked() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handle the moment a player's health exceeds 0
     * @param event - the event to handle
     */
    @EventHandler
    public void handlePlayerDeath(EntityDamageByEntityEvent event) {
        if(!event.getEntity().getClass().isInstance(Player.class)) {
            return; // Victim is not player, skip
        }
        if(!event.getDamager().getClass().isInstance(Player.class)) {
            return; // Killer is not player, skip
        }
        Player victim = (Player) event.getEntity();
        Player killer = (Player) event.getDamager();

        // Check if victim had died
        if(victim.getHealth() > 0) {
            return;
        }

        // Check if victim is part of a game and if the game is in running mode
        GameInstance instance = gameManagerService.getInstanceOf(victim);
        if(instance == null) {
            return;
        }
        if(instance.getCurrentStage().getName() != Stage.RUNNING) {
            return;
        }

        // Set health of victim to max and spectator
        handleVictim(victim, killer, instance);
        handleKiller(victim, killer, instance);

        // Remove victim from the instance
        instance.killPlayer(victim);
    }

    private void handleKiller(Player victim, Player killer, GameInstance instance) {
        var role = instance.getPlayerRoles().get(victim);
        instance.notifyPlayer(killer, "You killed %s [%s]".formatted(victim.getName(), role));
        // TODO KapitanFLoww add or remove karma
    }

    private void handleVictim(Player victim, Player killer, GameInstance instance) {
        var lobbyLocation = mapSpawnToLocation(instance.getLobby().getLobbySpawn());
        instance.heal(victim);
        instance.setGameMode(victim, GameMode.SPECTATOR);
        instance.teleport(victim, lobbyLocation);
        instance.notifyPlayer(victim, "You have been murdered by %s".formatted(killer.getName()));
    }
}
