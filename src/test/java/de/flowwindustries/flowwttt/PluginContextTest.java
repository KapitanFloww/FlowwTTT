package de.flowwindustries.flowwttt;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark this test as Spigot plugin test.
 * Will start up an in-memory H2 database and load config file from test resources.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(PluginContextTestExtension.class)
public @interface PluginContextTest {
}
