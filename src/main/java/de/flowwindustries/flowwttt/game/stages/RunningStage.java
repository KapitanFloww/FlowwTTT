package de.flowwindustries.flowwttt.game.stages;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.scheduled.Countdown;
import lombok.extern.java.Log;

import java.util.Objects;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_GAME_MAX_DURATION;
import static de.flowwindustries.flowwttt.config.FileConfigurationWrapper.readInt;
import static de.flowwindustries.flowwttt.domain.enumeration.GameResult.TIME_OUT;

@Log
public class RunningStage implements GameStage {

    private final int maxGameDuration;
    private final GameInstance gameInstance;

    private Countdown gameCountdown;

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
        gameCountdown = new Countdown(TTTPlugin.getInstance(),
                gameInstance.getIdentifier(),
                maxGameDuration,
                () -> {},
                this::timeout,
                t -> gameInstance.setLevelAll(t.getTimeLeft())
        );
        gameCountdown.scheduleCountdown();

    }

    @Override
    public void endStage() {
        log.info("%s stage ends for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        gameCountdown.cancel();
        gameInstance.setLevelAll(0);
    }

    private void timeout() {
        gameInstance.setGameResult(TIME_OUT);
        gameInstance.startNext();
    }
}
