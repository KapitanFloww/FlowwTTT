package de.flowwindustries.flowwttt;

import de.flowwindustries.flowwttt.commands.ArenaCommand;
import de.flowwindustries.flowwttt.commands.GameManagerCommand;
import de.flowwindustries.flowwttt.commands.LobbyCommand;
import de.flowwindustries.flowwttt.config.DefaultConfiguration;
import de.flowwindustries.flowwttt.domain.ArchivedGame;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import de.flowwindustries.flowwttt.listener.PlayerDamagerListener;
import de.flowwindustries.flowwttt.listener.PlayerMoveListener;
import de.flowwindustries.flowwttt.listener.StartInstanceListener;
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
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

@Log
public final class TTTPlugin extends JavaPlugin {

    public static final DecimalFormat COORDINATE_FORMATTER = new DecimalFormat("#.##");

    @Getter
    private static TTTPlugin instance;
    @Getter
    private FileConfiguration configuration;

    // Repositories
    private ArenaRepository arenaRepository;
    private LobbyRepository lobbyRepository;
    private ArchivedGameRepository archivedGameRepository;

    // Services
    private ArenaService arenaService;
    private LobbyService lobbyService;
    private GameManagerService gameManagerService;
    private ChestService chestService;

    @Override
    public void onEnable() {
        instance = this;
        setupConfig();
        setupRepositories();
        setupServices();
        setupCommands();
        setupListener();
        log.info("Initialization complete!");
    }

    private void setupConfig() {
        if(this.configuration == null) {
            this.configuration = this.getConfig();
        }
        DefaultConfiguration.setupDefaultConfiguration(this.getConfiguration());
        this.saveConfig();
    }

    private void setupRepositories() {
        this.arenaRepository = new ArenaRepository(Arena.class);
        this.lobbyRepository = new LobbyRepository(Lobby.class);
        this.archivedGameRepository = new ArchivedGameRepository(ArchivedGame.class);
    }

    private void setupServices() {
        // Services
        this.chestService = new ChestServiceImpl();
        this.arenaService = new ArenaServiceImpl(this.arenaRepository);
        this.lobbyService = new LobbyServiceImpl(this.lobbyRepository, arenaService);
        this.gameManagerService = new GameManagerServiceImpl(chestService, arenaService, archivedGameRepository);
    }

    private void setupCommands() {
        this.getCommand("arena").setExecutor(new ArenaCommand("ttt.arena", arenaService));
        this.getCommand("lobby").setExecutor(new LobbyCommand("ttt.lobby", lobbyService));
        this.getCommand("gm").setExecutor(new GameManagerCommand("ttt.gm", arenaService, lobbyService, gameManagerService));
    }

    private void setupListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerMoveListener(this.gameManagerService), this);
        pluginManager.registerEvents(new PlayerDamagerListener(this.gameManagerService), this);
        pluginManager.registerEvents(new StartInstanceListener(this.gameManagerService), this);
    }
}
