package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.items.ChestItem;

/**
 * Service responsible for the creation and delivery of {@link ChestItem}s.
 * May be used when a player clicks on a chest.
 */
public interface ItemService {

    /**
     * Load the {@link ChestItem}s from the configuration file.
     */
    void loadItemConfigurations();

    /**
     * Get a default {@link ChestItem}.
     */
    ChestItem getDefaultItem();

    /**
     * Get a legendary {@link ChestItem}.
     */
    ChestItem getLegendaryItem();

    /**
     * Get the amount of loaded default items.
     */
    Integer getDefaultItemsCount();

    /**
     * Get the amount of loaded legendary items.
     */
    Integer getLegendaryItemsCount();
}
