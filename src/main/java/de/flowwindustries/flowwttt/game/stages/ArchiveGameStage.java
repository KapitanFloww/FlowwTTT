package de.flowwindustries.flowwttt.game.stages;

import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.repository.ArchivedGameRepository;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log
public class ArchiveGameStage implements GameStage {

    private final GameInstance gameInstance;
    private final GameManagerService gameManagerService;
    private final ArchivedGameRepository archivedGameRepository;

    public ArchiveGameStage(GameInstance gameInstance, GameManagerService gameManagerService, ArchivedGameRepository archivedGameRepository) {
        this.gameInstance = Objects.requireNonNull(gameInstance);
        this.archivedGameRepository = Objects.requireNonNull(archivedGameRepository);
        this.gameManagerService = Objects.requireNonNull(gameManagerService);
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
                .withPlayerNames(gameInstance.getAllPlayers().toString())
                .withDeadPlayers(gameInstance.getRemovedPlayers().toString())
                .withAlivePlayers(gameInstance.getCurrentPlayersActive().toString())
                .withEndedAt(Instant.now(Clock.systemUTC()));
        archivedGameRepository.create(archivedGame);
        log.info("Archived game: %s".formatted(gameInstance.getIdentifier()));

        // End archive game stage
        endStage();
    }

    @Override
    public void endStage() {
        // Cache all players
        final List<Player> players = new ArrayList<>(this.gameInstance.getAllPlayers());
        // Cleanup this instance
        this.gameInstance.cleanup();
        // Create new instance
        gameManagerService.createInstance(gameInstance.getLobby());
        // Add cached players to new instance - if they're still online
        players.forEach(player -> {
            if (player.isOnline()) {
                gameManagerService.addPlayer(player);
            }
        });
    }
}
