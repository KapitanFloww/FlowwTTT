package de.flowwindustries.flowwttt.game.stages;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.game.GameStage;
import de.flowwindustries.flowwttt.scheduled.Countdown;
import de.flowwindustries.flowwttt.services.ChestService;
import de.flowwindustries.flowwttt.utils.SpigotParser;
import lombok.extern.java.Log;
import org.bukkit.GameMode;

import java.util.Objects;

@Log
public class EndgameStage implements GameStage {

    private final GameInstance gameInstance;
    private final ChestService chestService;

    private Countdown endgameCountdown;

    public EndgameStage(GameInstance gameInstance, ChestService chestService) {
        this.gameInstance = Objects.requireNonNull(gameInstance);
        this.chestService = Objects.requireNonNull(chestService);
    }

    @Override
    public Stage getName() {
        return Stage.ENDGAME;
    }

    @Override
    public Stage getNext() {
        return Stage.ARCHIVED;
    }

    @Override
    public void beginStage() {
        log.info("%s stage has begun for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        chestService.deSpawnChests(gameInstance.getArena());

        if(gameInstance.getGameResult() == GameResult.PENDING) {
            log.warning("GameResult still on %s. Changing to %s".formatted(GameResult.PENDING, GameResult.CANCELED));
            gameInstance.setGameResult(GameResult.CANCELED);
        }

        endgameCountdown = new Countdown(TTTPlugin.getInstance(),
                gameInstance.getIdentifier(),
                10,
                () -> {
                    gameInstance.notifyAllPlayers("*********************************");
                    gameInstance.notifyAllPlayers("Match has ended: %s".formatted(gameInstance.getGameResult()));
                    gameInstance.notifyAllPlayers("*********************************");
                },
                gameInstance::startNext,
                t -> {}
        );
        endgameCountdown.scheduleCountdown();
    }

    @Override
    public void endStage() {
        log.info("%s stage ends for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        endgameCountdown.cancel();

        gameInstance.healAll();
        gameInstance.clearAll();
        gameInstance.setGameModeAll(GameMode.ADVENTURE);

        var lobbyLocation = SpigotParser.mapSpawnToLocation(gameInstance.getLobby().getLobbySpawn());
        gameInstance.teleportAll(lobbyLocation);

        // TODO KapitanFloww create a new game instance and assign all online players to it
    }
}
