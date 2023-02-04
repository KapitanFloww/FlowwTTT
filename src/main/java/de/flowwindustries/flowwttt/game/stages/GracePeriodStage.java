package de.flowwindustries.flowwttt.game.stages;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.scheduled.Countdown;
import de.flowwindustries.flowwttt.services.RoleService;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;

import java.util.Objects;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_GRACE_PERIOD_DURATION;

@Log
public class GracePeriodStage implements GameStage {

    private final int gracePeriodDuration;
    private final GameInstance gameInstance;
    private final RoleService roleService;

    private Countdown gracePeriodCountdown;

    public GracePeriodStage(GameInstance gameInstance, RoleService roleService, FileConfigurationWrapper fileConfigurationWrapper) {
        this.gracePeriodDuration = Objects.requireNonNull(fileConfigurationWrapper).readInt(PATH_GAME_GRACE_PERIOD_DURATION);
        this.gameInstance = Objects.requireNonNull(gameInstance);
        this.roleService = Objects.requireNonNull(roleService);
    }

    @Override
    public Stage getName() {
        return Stage.GRACE_PERIOD;
    }

    @Override
    public Stage getNext() {
        return Stage.RUNNING;
    }

    @Override
    public void beginStage() {
        log.info("%s stage has begun for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        gracePeriodCountdown = new Countdown(TTTPlugin.getInstance(),
                gameInstance.getIdentifier(),
                gracePeriodDuration,
                () -> gameInstance.notifyAllPlayers("Grace Period has started! Roles will be assigned in %s seconds".formatted(gracePeriodDuration)),
                () -> {
                    gameInstance.notifyAllPlayers("Grace Period has ended!");
                    gameInstance.startNext();
                },
                t -> gameInstance.notifyAllPlayers("Grace Period ends in %s seconds".formatted(t.getTimeLeft())));
        gracePeriodCountdown.scheduleCountdown();
    }

    @Override
    public void endStage() {
        log.info("%s stage ends for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        assignRoles();
        gracePeriodCountdown.cancel();
    }

    private void assignRoles() {
        log.info("Assigning roles for %s players".formatted(gameInstance.getCurrentPlayerCount()));
        var roleAssignment = roleService.assignRoles(gameInstance.getCurrentPlayersActive().stream()
                .map(Player::getName)
                .toList());
        roleAssignment.forEach((playerName, role) -> {
            var player = mapPlayer(playerName);
            gameInstance.getPlayerRoles().put(player, role);
            notifyPlayerRole(player, role);
        });

        log.info("Roles have been assigned");
    }

    private static void notifyPlayerRole(Player player, Role role) {
        PlayerMessage.success("", player);
        PlayerMessage.success("########################", player);
        PlayerMessage.success("", player);
        PlayerMessage.success("You are assigned: %s".formatted(role), player);
        PlayerMessage.success("", player);
        PlayerMessage.success("########################", player);
        PlayerMessage.success("", player);
    }

    private Player mapPlayer(String playerName) {
        var playerResult = gameInstance.getCurrentPlayersActive().stream().filter(it -> it.getName().equals(playerName)).toList();
        if(playerResult.size() == 1) {
            return playerResult.get(0);
        }
        throw new IllegalStateException("Player %s not found in active players".formatted(playerName));
    }
}
