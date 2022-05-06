package de.flowwindustries.flowwttt.services;

import org.bukkit.entity.Player;

/**
 * Service responsible for the creation and delivery of {@link org.bukkit.inventory.ItemStack}s to {@link org.bukkit.entity.Player}s.
 * May be used when a player clicks on a chest.
 * Item configurations are being loaded from the configuration file.
 */
public interface ItemService {

    /**
     * Load the item set configurations from the configuration file.
     */
    void loadItemConfigurations();

    /**
     * Deliver a default set of items to this player.
     * @param player the player to deliver the items to
     */
    void getDefaultItems(Player player);

    /**
     * Deliver a legendary set of items to this player.
     * @param player the player to deliver the items to
     */
    void getLegendaryItems(Player player);
}
