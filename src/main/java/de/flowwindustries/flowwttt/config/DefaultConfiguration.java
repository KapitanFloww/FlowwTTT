package de.flowwindustries.flowwttt.config;

import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Util class to set the default configuration.
 */
@Log
public class DefaultConfiguration {

    public static final String PATH_DB_REMOTE = "database.remote";
    public static final String PATH_DB_SHOW_SQL = "database.show-sql";
    public static final String PATH_DB_DDL = "database.ddl-auto";
    public static final String PATH_DB_HOST = "database.datasource.host";
    public static final String PATH_DB_PORT = "database.datasource.port";
    public static final String PATH_DB_DATABASE = "database.datasource.database";
    public static final String PATH_DB_USERNAME = "database.datasource.username";
    public static final String PATH_DB_PASSWORD = "database.datasource.password";
    public static final String PATH_DEFAULT_LOBBY = "lobby.default.name";
    public static final String PATH_GAME_MAX_DURATION = "game.max-duration";
    public static final String PATH_GAME_MIN_PLAYERS = "game.players.min";

    /**
     * Setup default configuration values for the given {@link FileConfiguration}.
     * @param configuration the configuration file
     */
    public static void setupDefaultConfiguration(FileConfiguration configuration) {
        log.info("Setting up default configuration values");
        //Setup values
        configuration.addDefault(PATH_DB_REMOTE, true);
        configuration.addDefault(PATH_DB_SHOW_SQL, false);
        configuration.addDefault(PATH_DB_DDL, "update");

        configuration.addDefault(PATH_DB_HOST, "localhost");
        configuration.addDefault(PATH_DB_PORT, 5432);
        configuration.addDefault(PATH_DB_DATABASE, "flowwTTT");
        configuration.addDefault(PATH_DB_USERNAME, "flowwTTT");
        configuration.addDefault(PATH_DB_PASSWORD, "MyStr0ng!Passw0rd");

        configuration.addDefault(PATH_DEFAULT_LOBBY, "Lobby");

        configuration.addDefault(PATH_GAME_MAX_DURATION, 300);
        configuration.addDefault(PATH_GAME_MIN_PLAYERS, 4);
        //Save configuration
        configuration.options().copyDefaults(true);
    }
}
