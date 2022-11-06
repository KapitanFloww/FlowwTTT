package de.flowwindustries.flowwttt.config;

import de.flowwindustries.flowwttt.TTTPlugin;
import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Utility class to provide easy access to the {@link FileConfiguration}.
 */
@Log
public class FileConfigurationWrapper {

    private static FileConfiguration fileConfiguration;

    /**
     * Initialize.
     * @param configuration the configuration to wrap
     */
    public void ofConfiguration(FileConfiguration configuration) {
        fileConfiguration = configuration;
    }

    /**
     * Write a value to the configuration.
     * @param path the key / path
     * @param payload the value
     */
    public static void write(String path, Object payload) {
        fileConfiguration.set(path, payload);
        TTTPlugin.getInstance().saveConfig();
    }

    /**
     * Read configuration value.
     * @param clazz the return value type
     * @param path the key / path
     * @return the value
     * @param <T> the return value type
     */
    public static <T> T read(Class<T> clazz, String path) {
        Object payload = fileConfiguration.get(path);
        if(payload == null) {
            throw new RuntimeException("No configuration value found for path: " + path);
        }
        if(clazz.isInstance(payload)) {
            return (T) payload;
        }
        throw new RuntimeException("Invalid value type on path: " + path);
    }

    /**
     * Read this configuration value.
     * @param path the key / path
     * @return the {@link String} value
     */
    public static String readString(String path) {
        return read(String.class, path);
    }

    /**
     * Read this configuration value.
     * @param path the key / path
     * @return the {@link Boolean} value
     */
    public static boolean readBoolean(String path) {
        return read(Boolean.class, path);
    }

    /**
     * Read this configuration value.
     * @param path the key / path
     * @return the {@link Integer} value
     */
    public static int readInt(String path) {
        return read(Integer.class, path);
    }
}
