package de.flowwindustries.flowwttt;

import de.flowwindustries.flowwttt.config.DefaultConfiguration;
import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.repository.ArchivedGameRepository;
import de.flowwindustries.flowwttt.repository.ArenaRepository;
import de.flowwindustries.flowwttt.repository.LobbyRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.ChestService;
import de.flowwindustries.flowwttt.services.GameManagerService;
import de.flowwindustries.flowwttt.services.LobbyService;
import de.flowwindustries.flowwttt.services.impl.ArenaServiceImpl;
import de.flowwindustries.flowwttt.services.impl.ChestServiceImpl;
import de.flowwindustries.flowwttt.services.impl.GameManagerServiceImpl;
import de.flowwindustries.flowwttt.services.impl.LobbyServiceImpl;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

@Log
@Getter
public class PluginContext {

    private static PluginContext context;

    // Configuration
    private FileConfigurationWrapper configurationWrapper;

    // Repositories
    private ArenaRepository arenaRepository;
    private LobbyRepository lobbyRepository;
    private ArchivedGameRepository archivedGameRepository;

    // Services
    private ArenaService arenaService;
    private LobbyService lobbyService;
    private GameManagerService gameManagerService;
    private ChestService chestService;

    public PluginContext(FileConfiguration fileConfiguration, File configFile) {
        context = this;
        setupConfig(fileConfiguration, configFile);
        setupRepositories();
        setupServices();
    }

    private void setupConfig(FileConfiguration fileConfiguration, File configFile) {
        if(this.configurationWrapper == null) {
            this.configurationWrapper = new FileConfigurationWrapper();
            this.configurationWrapper.ofConfiguration(fileConfiguration);
        }
        DefaultConfiguration.setupDefaultConfiguration(fileConfiguration);
        try {
            log.info("Saving config file...");
            fileConfiguration.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupRepositories() {
        this.arenaRepository = new ArenaRepository(Arena.class);
        this.lobbyRepository = new LobbyRepository(Lobby.class);
        this.archivedGameRepository = new ArchivedGameRepository(ArchivedGame.class);

        log.info("Loading remote data...");
        log.info("Loading %s lobbies".formatted(lobbyRepository.findAll().size()));
        log.info("Loading %s arenas".formatted(arenaRepository.findAll().size()));
        log.info("Loading %s archived games".formatted(archivedGameRepository.findAll().size()));
    }

    private void setupServices() {
        // Services
        this.chestService = new ChestServiceImpl();
        this.arenaService = new ArenaServiceImpl(arenaRepository);
        this.lobbyService = new LobbyServiceImpl(arenaService, lobbyRepository);
        this.gameManagerService = new GameManagerServiceImpl(chestService, arenaService, archivedGameRepository);
    }

}

