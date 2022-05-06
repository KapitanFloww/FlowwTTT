package de.flowwindustries.flowwttt;

import de.flowwindustries.flowwttt.commands.ArenaCommand;
import de.flowwindustries.flowwttt.config.DefaultConfiguration;
import de.flowwindustries.flowwttt.domain.locations.Arena;
import de.flowwindustries.flowwttt.domain.locations.ChestSpawn;
import de.flowwindustries.flowwttt.domain.locations.PlayerSpawn;
import de.flowwindustries.flowwttt.repository.ArenaRepository;
import de.flowwindustries.flowwttt.repository.ChestSpawnRepository;
import de.flowwindustries.flowwttt.repository.PlayerSpawnRepository;
import de.flowwindustries.flowwttt.services.ArenaService;
import de.flowwindustries.flowwttt.services.impl.ArenaServiceImpl;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Log
public final class TTTPlugin extends JavaPlugin {
    @Getter
    private static TTTPlugin instance;
    @Getter
    private ArenaService arenaService;
    @Getter
    private FileConfiguration configuration;

    @Override
    public void onEnable() {
        instance = this;
        setupConfig();

        ArenaRepository arenaRepository = new ArenaRepository(Arena.class);
        PlayerSpawnRepository playerSpawnRepository = new PlayerSpawnRepository(PlayerSpawn.class);
        ChestSpawnRepository chestSpawnRepository = new ChestSpawnRepository(ChestSpawn.class);
        this.arenaService = new ArenaServiceImpl(arenaRepository, playerSpawnRepository, chestSpawnRepository);

        setupCommands();

        log.info("Initialization complete!");
    }

    private void setupCommands() {
        this.getCommand("arena").setExecutor(new ArenaCommand("ttt.arena", arenaService));
    }

    private void setupConfig() {
        if(this.configuration == null) {
            this.configuration = this.getConfig();
        }
        DefaultConfiguration.setupDefaultConfiguration(this.getConfiguration());
        this.saveConfig();
    }

    @Override
    public void onDisable() {
    }
}
