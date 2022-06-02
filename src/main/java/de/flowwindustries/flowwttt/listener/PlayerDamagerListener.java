package de.flowwindustries.flowwttt.listener;

import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.domain.GameInstance;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
public class PlayerDamagerListener implements Listener {

    private final GameManagerService gameManagerService;

    /**
     * Block damage of players when they are in a valid game instance and the stage is {@link Stage#COUNTDOWN}.
     * @param event the event to handle
     */
    @EventHandler
    public void onPlayerDamage(PlayerInteractEntityEvent event) {
        GameInstance instance = gameManagerService.getInstanceOf(event.getPlayer());
        if(instance == null) {
            return;
        }
        if(instance.getStage() == Stage.GRACE_PERIOD) {
            if(event.getRightClicked() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handle the moment a player's health exceeds 0
     * @param event the event to handle
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

        // Check if victim would have died
        if(victim.getHealth() > 0) {
            return;
        }

        // Check if victim is part of a game and if the game is in running mode
        GameInstance instance = gameManagerService.getInstanceOf(victim);
        if(instance == null) {
            return;
        }
        if(instance.getStage() != Stage.RUNNING) {
            return;
        }

        // Set health of victim to max and spectator
        handleVictim(victim, killer, instance);
        handleKiller(victim, killer, instance);

        // Remove victim from the instance
        instance.getCurrentPlayers().remove(victim);
    }

    private void handleKiller(Player victim, Player killer, GameInstance instance) {
        PlayerMessage.info(String.format("You killed %s [Role: %s]" + victim.getName(), "TODO"), killer); // TODO Add player role
        // TODO Get Player Roles and add or remove karma accordingly
    }

    private void handleVictim(Player victim, Player killer, GameInstance instance) {
        victim.setHealth(20.0d);
        victim.setGameMode(GameMode.SPECTATOR);
        Location lobbyLocation = mapSpawnToLocation(instance.getLobby().getLobbySpawn());
        victim.teleport(lobbyLocation);
        PlayerMessage.info("You have been murdered by " + killer.getName(), victim);
    }
}
