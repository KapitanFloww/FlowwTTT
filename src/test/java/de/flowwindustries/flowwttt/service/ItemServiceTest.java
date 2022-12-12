package de.flowwindustries.flowwttt.service;

import de.flowwindustries.flowwttt.PluginContextTest;
import de.flowwindustries.flowwttt.PluginContextTestExtension;
import de.flowwindustries.flowwttt.services.ItemService;
import de.flowwindustries.flowwttt.services.impl.ItemServiceImpl;
import org.assertj.core.api.Assertions;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

@PluginContextTest
public class ItemServiceTest {

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        var context = PluginContextTestExtension.getPluginContext();
        itemService = new ItemServiceImpl(context.getConfigurationWrapper());
    }

    @Test
    void verifyLoadItems() {
        // GIVEN
        // WHEN
        itemService.loadItemConfigurations();
        // THEN
        Assertions.assertThat(itemService.getDefaultItemsCount()).isEqualTo(4);
        Assertions.assertThat(itemService.getLegendaryItemsCount()).isEqualTo(2);
    }

    @RepeatedTest(10)
    void verifyGetDefaultItem() {
        // GIVEN
        itemService.loadItemConfigurations();
        // WHEN
        var item = itemService.getDefaultItem();
        // THEN
        Assertions.assertThat(item).isNotNull();
        Assertions.assertThat(item.getAmount()).isIn(1, 32);
        Assertions.assertThat(item.getName()).isIn("Training Sword", "Blunt Sword", "Hunting Bow", "Arrow Set");
        Assertions.assertThat(item.getMaterial()).isIn(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.BOW, Material.ARROW);
    }

    @RepeatedTest(10)
    void verifyGetLegendaryItem() {
        // GIVEN
        itemService.loadItemConfigurations();
        // WHEN
        var item = itemService.getLegendaryItem();
        // THEN
        Assertions.assertThat(item).isNotNull();
        Assertions.assertThat(item.getAmount()).isEqualTo(1);
        Assertions.assertThat(item.getName()).isIn("Steel Sword", "Katana");
        Assertions.assertThat(item.getMaterial()).isEqualTo(Material.IRON_SWORD);
    }
}
