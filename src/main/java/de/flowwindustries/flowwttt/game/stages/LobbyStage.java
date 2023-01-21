package de.flowwindustries.flowwttt.game.stages;

import de.flowwindustries.flowwttt.TTTPlugin;
import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.game.GameInstance;
import de.flowwindustries.flowwttt.game.events.EventSink;
import de.flowwindustries.flowwttt.scheduled.Idler;
import de.flowwindustries.flowwttt.services.ArenaService;
import lombok.extern.java.Log;

import java.util.Objects;

@Log
public class LobbyStage implements GameStage {

    private Idler lobbyIdler;
    private final ArenaService arenaService;
    private final GameInstance gameInstance;
    private final EventSink eventSink;
    private final FileConfigurationWrapper fileConfigurationWrapper;

    public LobbyStage(GameInstance gameInstance, ArenaService arenaService, EventSink eventSink, FileConfigurationWrapper fileConfigurationWrapper) {
        this.fileConfigurationWrapper = Objects.requireNonNull(fileConfigurationWrapper);
        this.gameInstance = Objects.requireNonNull(gameInstance);
        this.arenaService = Objects.requireNonNull(arenaService);
        this.eventSink = Objects.requireNonNull(eventSink);
    }

    @Override
    public Stage getName() {
        return Stage.LOBBY;
    }

    @Override
    public Stage getNext() {
        return Stage.COUNTDOWN;
    }

    @Override
    public void beginStage() {
        log.info("%s stage has begun for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        lobbyIdler = new Idler(TTTPlugin.getInstance(), gameInstance, arenaService, eventSink, fileConfigurationWrapper);
        lobbyIdler.schedule();
    }

    @Override
    public void endStage() {
        log.info("%s stage ends for instance: %s".formatted(getName(), gameInstance.getIdentifier()));
        lobbyIdler.cancel();
    }
}
