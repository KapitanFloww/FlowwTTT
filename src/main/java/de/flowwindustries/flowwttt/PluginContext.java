package de.flowwindustries.flowwttt;

import de.flowwindustries.flowwttt.config.DefaultConfiguration;
import de.flowwindustries.flowwttt.items.DefaultItemsConfig;
import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.game.listener.EventSink;
import de.flowwindustries.flowwttt.game.listener.FoodLevelChangeListener;
import de.flowwindustries.flowwttt.game.listener.ListenerRegistry;
import de.flowwindustries.flowwttt.game.listener.MatchEndListener;
import de.flowwindustries.flowwttt.game.listener.PlayerDamageListener;
import de.flowwindustries.flowwttt.game.listener.PlayerMoveListener;
import de.flowwindustries.flowwttt.game.listener.PlayerOpenChestListener;
import de.flowwindustries.flowwttt.game.listener.PluginContextEventSink;
import de.flowwindustries.flowwttt.game.listener.PluginContextRegistry;
import de.flowwindustries.flowwttt.game.listener.StartInstanceListener;
import de.flowwindustries.flowwttt.repository.ArchivedGameRepository;
import de.flowwindustries.flowwttt.repository.ArenaRepository;
import de.flowwindustries.flowwttt.repository.LobbyRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.ChestService;
import de.flowwindustries.flowwttt.services.GameManagerService;
import de.flowwindustries.flowwttt.services.ItemService;
import de.flowwindustries.flowwttt.services.LobbyService;
import de.flowwindustries.flowwttt.services.RoleService;
import de.flowwindustries.flowwttt.services.impl.ArenaServiceImpl;
import de.flowwindustries.flowwttt.services.impl.ChestServiceImpl;
import de.flowwindustries.flowwttt.services.impl.GameManagerServiceImpl;
import de.flowwindustries.flowwttt.services.impl.ItemServiceImpl;
import de.flowwindustries.flowwttt.services.impl.LobbyServiceImpl;
import de.flowwindustries.flowwttt.services.impl.RoleServiceImpl;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Log
@Getter
public class PluginContext {

    private final PluginManager pluginManager;
    private final Plugin plugin;

    private ListenerRegistry listenerRegistry;
    private EventSink eventSink;

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
    private ItemService itemService;
    private ChestService chestService;
    private RoleService roleService;

    public PluginContext(FileConfiguration fileConfiguration, File configFile, PluginManager pluginManager, Plugin plugin) {
        this.pluginManager = Objects.requireNonNull(pluginManager);
        this.plugin = Objects.requireNonNull(plugin);
        setupConfig(fileConfiguration, configFile);
        setupDefaultItems();
        setupEventSink();
        setupRepositories();
        setupServices();
        setupListeners();
    }

    private void setupConfig(FileConfiguration fileConfiguration, File configFile) {
        try {
            if(this.configurationWrapper == null) {
                this.configurationWrapper = new FileConfigurationWrapper(fileConfiguration);
            }
            DefaultConfiguration.setupDefaultConfiguration(fileConfiguration);
            log.info("Saving config file...");
            fileConfiguration.save(configFile);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save configuration: %s".formatted(e));
        }
    }

    private void setupDefaultItems() {
        DefaultItemsConfig.init(this.configurationWrapper);
        DefaultItemsConfig.copyDefaultsIfNotExist();
    }

    private void setupEventSink() {
        this.eventSink = new PluginContextEventSink(pluginManager);
    }

    private void setupRepositories() {
        this.arenaRepository = new ArenaRepository(Arena.class, configurationWrapper);
        this.lobbyRepository = new LobbyRepository(Lobby.class, configurationWrapper);
        this.archivedGameRepository = new ArchivedGameRepository(ArchivedGame.class, configurationWrapper);

        log.info("Loading remote data...");
        try {
            log.info("Loading %s lobbies".formatted(lobbyRepository.findAll().size()));
            log.info("Loading %s arenas".formatted(arenaRepository.findAll().size()));
            log.info("Loading %s archived games".formatted(archivedGameRepository.findAll().size()));
        } catch (Exception ex) {
            log.severe("Could not connect to database: %s".formatted(ex.getMessage()));
        }
    }

    private void setupServices() {
        this.itemService = new ItemServiceImpl(configurationWrapper);
        this.chestService = new ChestServiceImpl();
        this.arenaService = new ArenaServiceImpl(arenaRepository);
        this.lobbyService = new LobbyServiceImpl(arenaService, lobbyRepository, configurationWrapper);
        this.roleService = new RoleServiceImpl(getRoleRatios());
        this.gameManagerService = new GameManagerServiceImpl(chestService, arenaService, roleService, archivedGameRepository, eventSink, configurationWrapper);
    }

    private void setupListeners() {
        listenerRegistry = new PluginContextRegistry(pluginManager, plugin);
        listenerRegistry.registerListener(new FoodLevelChangeListener(gameManagerService));
        listenerRegistry.registerListener(new PlayerMoveListener(gameManagerService));
        listenerRegistry.registerListener(new PlayerDamageListener(gameManagerService, eventSink));
        listenerRegistry.registerListener(new StartInstanceListener(gameManagerService));
        listenerRegistry.registerListener(new MatchEndListener(gameManagerService, eventSink));
        listenerRegistry.registerListener(new PlayerOpenChestListener(itemService, gameManagerService));
    }

    private static Map<Role, Float> getRoleRatios() {
        var ratios = new EnumMap<Role, Float>(Role.class);
        ratios.put(Role.TRAITOR, 0.30f);
        ratios.put(Role.DETECTIVE, 0.10f);
        ratios.put(Role.INNOCENT, 0.60f);
        return ratios;
    }

}

