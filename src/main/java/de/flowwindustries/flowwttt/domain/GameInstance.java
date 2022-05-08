package de.flowwindustries.flowwttt.domain;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A game instance.
 * I.e: a match
 */
@Log
public class GameInstance {

    /**
     * This game's identifier.
     */
    private String identifier;

    /**
     * The current stage this game is in.
     */
    private Stage stage;

    /**
     * The arena this game takes place.
     */
    private Arena arena;

    /**
     * The lobby the players will be taken after finishing the game.
     */
    private Lobby lobby;

    /**
     * Map of players and their role.
     */
    private Map<Player, Role> playerRoles;

    /**
     * Current players.
     */
    private final List<Player> players = new ArrayList<>();

    /**
     * Initialize the roles for the current players.
     */
    public void initializeRoles() {
        int playerSize = players.size();
        log.config("Assigning roles for " + playerSize + " players");

        // TODO: Assign roles
        // Call RoleService and fetch the map of roles and players
        // 10% Detective
        // 60% Innocent
        // 30% Traitor
    }

    /**
     * Add a player to this game instance.
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        players.add(player);
        log.config("Added player " + player.getName() + " to game instance " + this.getIdentifier());
    }

    /**
     * Remove a player from this game instance.
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        players.remove(player);
        log.config("Removed player " + player.getName() + " to game instance " +  this.getIdentifier());
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        switch (stage) { //TODO implement logic
            case LOBBY -> initializeCountdown();
            case COUNTDOWN -> initializeGracePeriod();
            case GRACE_PERIOD -> System.out.println("Grace-Period complete");
            case RUNNING -> System.out.println("Running complete");
            case ENDGAME -> System.out.println("Endgame complete");
        }
        this.stage = stage;
    }

    private void initializeCountdown() {
        // Teleport players to their spawns
        log.info("Initializing COUNTDOWN stage");
        for(int i=0; i<this.players.size(); i++) {
            Player player = this.players.get(i);
            PlayerSpawn spawn = this.getArena().getPlayerSpawns().get(i);

            World world = Bukkit.getWorld(spawn.getWorldName());
            Location location = new Location(world, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());

            player.teleport(location);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        }

        //TODO disable player move

        // Start the countdown
        BukkitRunnable countdown = new BukkitRunnable() {
            private int timer = 30;

            @Override
            public void run() {
                if(timer <= 0) {
                    this.cancel();
                }
                PlayerMessage.info("Match is starting in " + timer + "s", players);
                if(timer <= 10) {
                    players.forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f));
                }
                timer--;
            }
        };
        countdown.runTaskTimer(TTTPlugin.getInstance(), 0, 20); // 20 ticks = 1 second
        this.setStage(Stage.COUNTDOWN);
    }

    private void initializeGracePeriod() {
        log.info("Initialize GRACE_PERIOD stage");
        // TODO Enable player move
        // TODO Disable pvp
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }
}
