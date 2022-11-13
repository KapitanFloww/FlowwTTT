package de.flowwindustries.flowwttt.game.stages;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.game.GameStage;
import de.flowwindustries.flowwttt.scheduled.Countdown;
import lombok.extern.java.Log;

import java.util.Objects;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_MAX_DURATION;
import static de.flowwindustries.flowwttt.config.FileConfigurationWrapper.readInt;
import static de.flowwindustries.flowwttt.domain.enumeration.GameResult.PENDING;
import static de.flowwindustries.flowwttt.domain.enumeration.GameResult.TIME_OUT;
import static de.flowwindustries.flowwttt.domain.enumeration.GameResult.TRAITOR_WIN;

@Log
public class RunningStage implements GameStage {

    private final int maxGameDuration;
    private final GameInstance gameInstance;

    public RunningStage(GameInstance gameInstance) {
        this.maxGameDuration = readInt(PATH_GAME_MAX_DURATION);
        this.gameInstance = Objects.requireNonNull(gameInstance);
    }

    @Override
    public Stage getName() {
        return Stage.RUNNING;
    }

    @Override
    public Stage getNext() {
        return Stage.ENDGAME;
    }

    @Override
    public void beginStage() {
        log.info("%s stage has begun for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        Countdown countdown = new Countdown(TTTPlugin.getInstance(),
                gameInstance.getIdentifier(),
                maxGameDuration,
                () -> {},
                this::timeout,
                t -> gameInstance.setLevelAll(t.getTimeLeft())
        );
        countdown.scheduleCountdown();

    }

    @Override
    public void endStage() {
        log.info("%s stage ends for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
    }

    private void recalculateGameResult() {
        if (gameInstance.getGameResult() != PENDING) {
            throw new IllegalStateException("GameResult is already set to: %s".formatted(gameInstance.getGameResult()));
        }

        // Check for traitor win
        var traitorsCount = getRoleCount(Role.TRAITOR);
        if(traitorsCount == 0) {
            innocentsWin();
            return;
        }

        // Check for inno win
        var innocentsCount = getRoleCount(Role.INNOCENT);
        var detectiveCount = getRoleCount(Role.DETECTIVE);
        if(innocentsCount + detectiveCount == 0) {
            traitorsWin();
        }
    }

    private int getRoleCount(Role role) {
        return gameInstance.getCurrentPlayersActive().stream()
                .filter(player -> gameInstance.getPlayerRoles().get(player) == role)
                .toList()
                .size();
    }

    private void innocentsWin() {
        gameInstance.setGameResult(GameResult.INNOCENT_WIN);
        gameInstance.startNext();
    }

    private void traitorsWin() {
        gameInstance.setGameResult(TRAITOR_WIN);
        gameInstance.startNext();
    }

    private void timeout() {
        gameInstance.setGameResult(TIME_OUT);
        gameInstance.startNext();
    }
}
