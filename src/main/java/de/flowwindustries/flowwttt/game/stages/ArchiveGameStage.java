package de.flowwindustries.flowwttt.game.stages;

import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.repository.ArchivedGameRepository;
import lombok.extern.java.Log;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

@Log
public class ArchiveGameStage implements GameStage {

    private final GameInstance gameInstance;
    private final ArchivedGameRepository archivedGameRepository;

    public ArchiveGameStage(GameInstance gameInstance, ArchivedGameRepository archivedGameRepository) {
        this.gameInstance = Objects.requireNonNull(gameInstance);
        this.archivedGameRepository = Objects.requireNonNull(archivedGameRepository);
    }

    @Override
    public Stage getName() {
        return Stage.ARCHIVED;
    }

    @Override
    public Stage getNext() {
        return null;
    }

    @Override
    public void beginStage() {
        log.info("%s stage has begun for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        var archivedGame = new ArchivedGame()
                .withInstanceId(gameInstance.getIdentifier())
                .withGameResult(gameInstance.getGameResult())
                .withStage(gameInstance.getCurrentStage().getName())
                .withLobbyName(gameInstance.getLobby().getLobbyName())
                .withArenaName(gameInstance.getArena().getArenaName())
                .withPlayerNames(gameInstance.getCurrentPlayersActive().toString())
                .withEndedAt(Instant.now(Clock.systemUTC()));
        archivedGameRepository.create(archivedGame);
        log.info("Archived game: %s".formatted(gameInstance.getIdentifier()));
    }

    @Override
    public void endStage() {}
}
