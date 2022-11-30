package de.flowwindustries.flowwttt.game.listener;

import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.events.PlayerReduceEvent;
import de.flowwindustries.flowwttt.events.ReductionType;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static de.flowwindustries.flowwttt.utils.SpigotParser.mapSpawnToLocation;

/**
 * Listen to a player death.
 */
@Log
@RequiredArgsConstructor
public class PlayerDamageListener implements Listener {

    private final GameManagerService gameManagerService;

    /**
     * Block damage to players when they are in a valid game instance and the stage is not {@link Stage#RUNNING}.
     * Handles "death" of a player when being killed by another player.
     * @param event - the event to handle
     */
    @EventHandler
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player player)) {
            log.fine("Damaged entity a not player. Skipping event handling...");
            return;
        }

        if(!(event.getDamager() instanceof Player damager)) {
            log.fine("Damager not a player. Skipping event handling...");
            return;
        }

        GameInstance instance = gameManagerService.getInstanceOf(player);
        if(instance == null) {
            log.fine("Player %s is not in a valid game instance".formatted(player.getName()));
            return;
        }
        if(instance.getCurrentStage().getName() == Stage.RUNNING) {
            handleDeath(event, instance, player, damager);
            return;
        }
        log.fine("Ignoring damage for player %s".formatted(player.getName()));
        event.setDamage(0.0d);
        event.setCancelled(true);
    }

    /**
     * Handle the case that a player would kill another player
     */
    private void handleDeath(final EntityDamageByEntityEvent event, final GameInstance instance, final Player victim, final Player damager) {
        if((victim.getHealth() - event.getDamage()) >= 0) {
            log.info("Handling player kill: Victim: %s Killer: %s".formatted(victim.getName(), damager.getName()));

            // Disable the event
            event.setDamage(0.0d);
            event.setCancelled(true);

            handleVictim(victim, damager, instance);
            handleKiller(victim, damager, instance);

            PlayerReduceEvent reduceEvent = new PlayerReduceEvent(instance, ReductionType.DEATH, victim);
            Bukkit.getServer().getPluginManager().callEvent(reduceEvent);
        }
    }

    private void handleKiller(Player victim, Player killer, GameInstance instance) {
        var role = instance.getPlayerRoles().get(victim);
        instance.notifyPlayer(killer, "You killed %s [%s]".formatted(victim.getName(), role));
        // TODO KapitanFLoww add or remove karma
    }

    private void handleVictim(Player victim, Player killer, GameInstance instance) {
        instance.notifyPlayer(victim, "You have been murdered by %s".formatted(killer.getName()));
        instance.heal(victim);
        instance.setGameMode(victim, GameMode.SPECTATOR);
        var lobbyLocation = mapSpawnToLocation(instance.getLobby().getLobbySpawn());
        instance.teleport(victim, lobbyLocation);
    }
}
