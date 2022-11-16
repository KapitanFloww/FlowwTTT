package de.flowwindustries.flowwttt;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;

/**
 * Creates a new {@link PluginContext} for this test.
 * Loads configuration file from /src/test/resources.
 */
public class PluginContextTestExtension implements BeforeAllCallback {

    private static final String TEST_CONFIG_FILE_PATH = "src/test/resources/config.yml";

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        var testConfig = new YamlConfiguration();
        testConfig.load(TEST_CONFIG_FILE_PATH);
        new PluginContext(testConfig, new File(TEST_CONFIG_FILE_PATH), new TestPluginManager(), new TestPlugin());
    }
}
