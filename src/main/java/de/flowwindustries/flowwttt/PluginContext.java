package de.flowwindustries.flowwttt;

import de.flowwindustries.flowwttt.config.DefaultConfiguration;
import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.game.listener.ListenerRegistry;
import de.flowwindustries.flowwttt.game.listener.MatchEndListener;
import de.flowwindustries.flowwttt.game.listener.PlayerDamageListener;
import de.flowwindustries.flowwttt.game.listener.PlayerMoveListener;
import de.flowwindustries.flowwttt.game.listener.PluginContextRegistry;
import de.flowwindustries.flowwttt.game.listener.StartInstanceListener;
import de.flowwindustries.flowwttt.repository.ArchivedGameRepository;
import de.flowwindustries.flowwttt.repository.ArenaRepository;
import de.flowwindustries.flowwttt.repository.LobbyRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.ChestService;
import de.flowwindustries.flowwttt.services.GameManagerService;
import de.flowwindustries.flowwttt.services.LobbyService;
import de.flowwindustries.flowwttt.services.RoleService;
import de.flowwindustries.flowwttt.services.impl.ArenaServiceImpl;
import de.flowwindustries.flowwttt.services.impl.ChestServiceImpl;
import de.flowwindustries.flowwttt.services.impl.GameManagerServiceImpl;
import de.flowwindustries.flowwttt.services.impl.LobbyServiceImpl;
import de.flowwindustries.flowwttt.services.impl.RoleServiceImpl;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log
@Getter
public class PluginContext {

    private static PluginContext context;

    private PluginManager pluginManager;
    private Plugin plugin;

    private ListenerRegistry listenerRegistry;

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
    private RoleService roleService;

    public PluginContext(FileConfiguration fileConfiguration, File configFile, PluginManager pluginManager, Plugin plugin) {
        context = this;
        this.pluginManager = Objects.requireNonNull(pluginManager);
        this.plugin = Objects.requireNonNull(plugin);
        setupConfig(fileConfiguration, configFile);
        setupRepositories();
        setupServices();
        setupListeners();
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
        try {
            log.info("Loading %s lobbies".formatted(lobbyRepository.findAll().size()));
            log.info("Loading %s arenas".formatted(arenaRepository.findAll().size()));
            log.info("Loading %s archived games".formatted(archivedGameRepository.findAll().size()));
        } catch (Exception ex) {
            log.severe("Could not connect to database: %s".formatted(ex.getMessage()));
        }
    }

    private void setupListeners() {
        listenerRegistry = new PluginContextRegistry(pluginManager, plugin);
        listenerRegistry.registerListener(new PlayerMoveListener(context.getGameManagerService()));
        listenerRegistry.registerListener(new PlayerDamageListener(context.getGameManagerService()));
        listenerRegistry.registerListener(new StartInstanceListener(context.getGameManagerService()));
        listenerRegistry.registerListener(new MatchEndListener(context.getGameManagerService()));
    }

    private void setupServices() {
        this.chestService = new ChestServiceImpl();
        this.arenaService = new ArenaServiceImpl(arenaRepository);
        this.lobbyService = new LobbyServiceImpl(arenaService, lobbyRepository);
        this.roleService = new RoleServiceImpl(getRoleRatios());
        this.gameManagerService = new GameManagerServiceImpl(chestService, arenaService, roleService, archivedGameRepository);
    }

    private static Map<Role, Float> getRoleRatios() {
        var ratios = new HashMap<Role, Float>();
        ratios.put(Role.TRAITOR, 0.30f);
        ratios.put(Role.DETECTIVE, 0.10f);
        ratios.put(Role.INNOCENT, 0.60f);
        return ratios;
    }

}

