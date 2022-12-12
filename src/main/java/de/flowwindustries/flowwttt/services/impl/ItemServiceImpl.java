package de.flowwindustries.flowwttt.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import de.flowwindustries.flowwttt.domain.items.ChestItem;
import de.flowwindustries.flowwttt.domain.items.ChestItemJson;
import de.flowwindustries.flowwttt.services.ItemService;
import lombok.extern.java.Log;
import org.bukkit.Material;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_ITEMS;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_LEGENDARY_ITEMS;

@Log
public class ItemServiceImpl implements ItemService {

    private static final Random RANDOM = new Random();

    private final ObjectMapper objectMapper;
    private final FileConfigurationWrapper fileConfigurationWrapper;

    private ChestItem[] defaultItems;
    private ChestItem[] legendaryItems;

    public ItemServiceImpl(FileConfigurationWrapper fileConfigurationWrapper) {
        this.fileConfigurationWrapper = Objects.requireNonNull(fileConfigurationWrapper);
        this.objectMapper = new ObjectMapper();
        loadItemConfigurations();
    }

    @Override
    public void loadItemConfigurations() {
        log.info("Loading items...");
        this.defaultItems = loadItems(fileConfigurationWrapper.readString(PATH_ITEMS));
        this.legendaryItems = loadItems(fileConfigurationWrapper.readString(PATH_LEGENDARY_ITEMS));
    }

    @Override
    public ChestItem getDefaultItem() {
        return getRandomItem(defaultItems);
    }

    @Override
    public ChestItem getLegendaryItem() {
        return getRandomItem(legendaryItems);
    }

    @Override
    public Integer getDefaultItemsCount() {
        return this.defaultItems.length;
    }

    @Override
    public Integer getLegendaryItemsCount() {
        return this.legendaryItems.length;
    }

    private static ChestItem getRandomItem(ChestItem[] items) {
        int index = RANDOM.nextInt(0, items.length);
        var item = items[index];
        log.info("Returning item: %s".formatted(item));
        return item;
    }

    private ChestItem[] loadItems(String path) {
        try (final InputStream itemStream = new FileInputStream(path)) {
            var items = objectMapper.readValue(itemStream, ChestItemJson[].class);
            return chestItemFunction.apply(items);
        } catch (IOException ex) {
            log.warning("Could not load items: %s".formatted(ex.getMessage()));
        }
        return new ChestItem[0];
    }

    private final Function<ChestItemJson[], ChestItem[]> chestItemFunction = chestItemJsons -> Arrays.stream(chestItemJsons)
            .map(chestItemJson -> {
                Material material = Optional.ofNullable(Material.getMaterial(chestItemJson.getMaterial()))
                        .orElseThrow(() -> new IllegalStateException("Could not convert material: %s".formatted(chestItemJson.getMaterial())));
                return new ChestItem(chestItemJson.getName(), material, chestItemJson.getAmount());
            })
            .toArray(ChestItem[]::new);
}
