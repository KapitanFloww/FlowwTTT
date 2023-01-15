package de.flowwindustries.flowwttt.config;

import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Util class to set the default configuration.
 */
@Log
public class DefaultConfiguration {

    public static final String PATH_HIBERNATE_SHOW_SQL = "database.hibernate.show-sql";
    public static final String PATH_HIBERNATE_DDL_AUTO = "database.hibernate.ddl-auto";
    public static final String PATH_HIBERNATE_DIALECT = "database.hibernate.dialect";
    public static final String PATH_HIBERNATE_CONNECTION_PROVIDER = "database.hibernate.connection-provider";

    public static final String PATH_DATASOURCE_JDBC_URL = "database.jdbc.url";
    public static final String PATH_DATASOURCE_JDBC_DRIVER = "database.jdbc.driver";
    public static final String PATH_DATASOURCE_JDBC_USERNAME = "database.jdbc.username";
    public static final String PATH_DATASOURCE_JDBC_PASSWORD = "database.jdbc.password";

    public static final String PATH_GAME_DEFAULT_LOBBY = "lobby.default.name";
    public static final String PATH_GAME_MAX_DURATION = "game.max-duration";
    public static final String PATH_GAME_LOBBY_COUNTDOWN_DURATION = "game.lobby-countdown-duration";
    public static final String PATH_GAME_GRACE_PERIOD_DURATION = "game.grace-period-duration";
    public static final String PATH_GAME_MIN_PLAYERS = "game.players.min";

    public static final String PATH_ITEMS = "game.items.path.default";
    public static final String PATH_LEGENDARY_ITEMS = "game.items.path.legendary";

    private DefaultConfiguration() {}

    /**
     * Setup default configuration values for the given {@link FileConfiguration}.
     * @param configuration the configuration file
     */
    public static void setupDefaultConfiguration(FileConfiguration configuration) {
        log.info("Setting up default configuration values");
        //Setup values
        configuration.addDefault(PATH_HIBERNATE_SHOW_SQL, false);
        configuration.addDefault(PATH_HIBERNATE_DDL_AUTO, "update");
        configuration.addDefault(PATH_HIBERNATE_DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        configuration.addDefault(PATH_HIBERNATE_CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        configuration.addDefault(PATH_DATASOURCE_JDBC_DRIVER, "org.postgresql.Driver");
        configuration.addDefault(PATH_DATASOURCE_JDBC_URL, "jdbc:postgresql://localhost:5432/flowwTTT?autoReconnect=true");
        configuration.addDefault(PATH_DATASOURCE_JDBC_USERNAME, "flowwTTT");
        configuration.addDefault(PATH_DATASOURCE_JDBC_PASSWORD, "MyStr0ng!Passw0rd");

        configuration.addDefault(PATH_GAME_DEFAULT_LOBBY, "Lobby");
        configuration.addDefault(PATH_GAME_MAX_DURATION, 300);
        configuration.addDefault(PATH_GAME_GRACE_PERIOD_DURATION, 30);
        configuration.addDefault(PATH_GAME_LOBBY_COUNTDOWN_DURATION, 30);
        configuration.addDefault(PATH_GAME_MIN_PLAYERS, 4);

        configuration.addDefault(PATH_ITEMS, "plugins/FlowwTTT/default-items.json");
        configuration.addDefault(PATH_LEGENDARY_ITEMS, "plugins/FlowwTTT/legendary-items.json");
        //Save configuration
        configuration.options().copyDefaults(true);
    }
}
