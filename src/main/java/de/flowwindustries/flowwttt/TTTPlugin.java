package de.flowwindustries.flowwttt;

import de.flowwindustries.flowwttt.commands.ArenaCommand;
import de.flowwindustries.flowwttt.commands.GameManagerCommand;
import de.flowwindustries.flowwttt.commands.LobbyCommand;
import de.flowwindustries.flowwttt.commands.debug.RunningTasksCommand;
import de.flowwindustries.flowwttt.game.listener.MatchEndListener;
import de.flowwindustries.flowwttt.game.listener.PlayerDamageListener;
import de.flowwindustries.flowwttt.game.listener.PlayerMoveListener;
import de.flowwindustries.flowwttt.game.listener.StartInstanceListener;
import de.flowwindustries.flowwttt.repository.AbstractRepository;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

@Log
public final class TTTPlugin extends JavaPlugin {

    private static final String CONFIG_PATH = "./plugins/FlowwTTT/config.yml";
    public static final DecimalFormat COORDINATE_FORMATTER = new DecimalFormat("#.##");

    @Getter
    private static TTTPlugin instance;
    private PluginContext context;

    @Override
    public void onEnable() {
        instance = this;
        context = new PluginContext(this.getConfig(), new File(CONFIG_PATH));
        setupCommands();
        setupListener();
        log.info("Initialization complete!");
    }

    @Override
    public void onDisable() {
        AbstractRepository.closeEntityManagerFactory();
    }

    private void setupCommands() {
        Objects.requireNonNull(this.getCommand("tasks")).setExecutor(new RunningTasksCommand("ttt.debug"));
        Objects.requireNonNull(this.getCommand("arena")).setExecutor(new ArenaCommand("ttt.arena", context.getArenaService()));
        Objects.requireNonNull(this.getCommand("lobby")).setExecutor(new LobbyCommand("ttt.lobby", context.getLobbyService()));
        Objects.requireNonNull(this.getCommand("gm")).setExecutor(new GameManagerCommand("ttt.gm",
                context.getArenaService(), context.getLobbyService(), context.getGameManagerService()));
    }

    private void setupListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerMoveListener(context.getGameManagerService()), this);
        pluginManager.registerEvents(new PlayerDamageListener(context.getGameManagerService()), this);
        pluginManager.registerEvents(new StartInstanceListener(context.getGameManagerService()), this);
        pluginManager.registerEvents(new MatchEndListener(context.getGameManagerService()), this);
    }
}
