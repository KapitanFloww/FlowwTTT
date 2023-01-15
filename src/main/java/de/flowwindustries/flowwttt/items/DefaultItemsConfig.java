package de.flowwindustries.flowwttt.items;

import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_ITEMS;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_LEGENDARY_ITEMS;

@Log
public class DefaultItemsConfig {

    private static FileConfigurationWrapper configurationWrapper;

    private DefaultItemsConfig() {}

    public static void init(FileConfigurationWrapper fileConfigurationWrapper) {
        configurationWrapper = fileConfigurationWrapper;
    }

    public static void copyDefaultsIfNotExist() {
        Objects.requireNonNull(configurationWrapper);
        try {
            final File itemsFile = Path.of(configurationWrapper.readString(PATH_ITEMS)).toFile();
            final File legendaryItemsFile = Path.of(configurationWrapper.readString(PATH_LEGENDARY_ITEMS)).toFile();

            createDefaultFile(itemsFile, ItemJsonDefaults.DEFAULT_ITEMS);
            createDefaultFile(legendaryItemsFile, ItemJsonDefaults.LEGENDARY_ITEMS);
        } catch (IOException ex) {
            log.severe("Could not initialize items: %s".formatted(ex.getMessage()));
            ex.printStackTrace();
        }
    }

    private static void createDefaultFile(File file, String defaults) throws IOException {
        if(file.exists()) {
            log.info("%s found".formatted(file.getAbsolutePath()));
            return;
        }
        try (final Writer writer = new FileWriter(file)) {
            file.createNewFile();
            writer.write(defaults);
            log.info("Created file %s".formatted(file.getAbsolutePath()));
        }
    }
}
