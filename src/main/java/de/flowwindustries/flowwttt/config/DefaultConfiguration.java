package de.flowwindustries.flowwttt.config;

import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Util class to set the default configuration.
 */
@Log
public class DefaultConfiguration {

    /**
     * Setup default configuration values for the given {@link FileConfiguration}.
     * @param configuration the configuration file
     */
    public static void setupDefaultConfiguration(FileConfiguration configuration) {
        log.info("Setting up default configuration values");
        //Setup values
        configuration.addDefault("database.remote", true);
        configuration.addDefault("database.show-sql", false);
        configuration.addDefault("database.ddl-auto", "update");

        configuration.addDefault("database.mariadb.host", "localhost");
        configuration.addDefault("database.mariadb.port", 3306);
        configuration.addDefault("database.mariadb.database", "ttt");
        configuration.addDefault("database.mariadb.username", "ttt");
        configuration.addDefault("database.mariadb.password", "password");
        //Save configuration
        configuration.options().copyDefaults(true);
    }
}
