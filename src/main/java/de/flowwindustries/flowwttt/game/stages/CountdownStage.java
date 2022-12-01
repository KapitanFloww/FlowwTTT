package de.flowwindustries.flowwttt.game.stages;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.scheduled.Countdown;
import de.flowwindustries.flowwttt.services.ChestService;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_LOBBY_COUNTDOWN_DURATION;
import static de.flowwindustries.flowwttt.config.FileConfigurationWrapper.readInt;

@Log
public class CountdownStage implements GameStage {

    private final int countDownDuration;
    private final GameInstance gameInstance;
    private final ChestService chestService;

    private Countdown lobbyCountdown;

    public CountdownStage(GameInstance gameInstance, ChestService chestService) {
        this.countDownDuration = readInt(PATH_GAME_LOBBY_COUNTDOWN_DURATION);
        this.gameInstance = Objects.requireNonNull(gameInstance);
        this.chestService = Objects.requireNonNull(chestService);
    }

    @Override
    public Stage getName() {
        return Stage.COUNTDOWN;
    }

    @Override
    public Stage getNext() {
        return Stage.GRACE_PERIOD;
    }

    @Override
    public void beginStage() {
        log.info("%s stage has begun for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        teleportPlayerToSpawns();
        lobbyCountdown = new Countdown(TTTPlugin.getInstance(),
                gameInstance.getIdentifier(),
                countDownDuration,
                () -> gameInstance.notifyAllPlayers("The match will start soon! Get ready!"),
                () -> {
                    gameInstance.notifyAllPlayers("The match has started!");
                    gameInstance.startNext();
                },
                t -> gameInstance.notifyAllPlayers("Match will start in %s seconds".formatted(t.getTimeLeft())));
        lobbyCountdown.scheduleCountdown();

        // De-spawn Chests
        chestService.spawnChests(gameInstance.getArena());
    }

    @Override
    public void endStage() {
        log.info("%s stage ends for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        lobbyCountdown.cancel();
    }

    private void teleportPlayerToSpawns() {
        for(int i=0; i < gameInstance.getCurrentPlayersActive().size(); i++) {
            Player player = gameInstance.getCurrentPlayersActive().get(i);
            PlayerSpawn spawn = gameInstance.getArena().getPlayerSpawns().get(i);

            World world = Bukkit.getWorld(spawn.getWorldName());
            Location location = new Location(world, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());

            player.teleport(location);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);

            gameInstance.setGameMode(player, GameMode.ADVENTURE);
            gameInstance.setLevel(player, 0);
            gameInstance.clearInventory(player);
            gameInstance.heal(player);
            gameInstance.feed(player);
        }
    }
}
