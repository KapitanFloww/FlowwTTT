package de.flowwindustries.flowwttt.game.events.listener.chest;

import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import de.flowwindustries.flowwttt.domain.items.ChestItem;
import de.flowwindustries.flowwttt.services.GameManagerService;
import de.flowwindustries.flowwttt.services.ItemService;
import lombok.extern.java.Log;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

@Log
public class PlayerOpenChestListener implements Listener {

    private final ItemService itemService;
    private final GameManagerService gameManagerService;

    public PlayerOpenChestListener(ItemService itemService, GameManagerService gameManagerService) {
        this.itemService = Objects.requireNonNull(itemService);
        this.gameManagerService = Objects.requireNonNull(gameManagerService);
    }

    @EventHandler
    public void handleChestOpening(PlayerInteractEvent event) {
        // Determine clicked block
        if(!event.hasBlock()) {
            return;
        }
        var block = event.getClickedBlock();
        if(block == null) {
            return;
        }
        if(block.getType() == Material.CHEST || block.getType() == Material.ENDER_CHEST) {
            // Determine player & instance
            var player = event.getPlayer();
            if (!gameManagerService.isInCurrentInstance(player)) {
                log.info("Player %s is not in current instance. Skipping".formatted(player.getName()));
                return;
            }
            var instance = gameManagerService.getCurrentInstance();
            if(instance.getCurrentStage().getName() != Stage.RUNNING && instance.getCurrentStage().getName() != Stage.GRACE_PERIOD) {
                return;
            }

            event.setCancelled(true);
            player.closeInventory();

            ChestItem item = null;
            if(block.getType() == Material.CHEST) {
                item = itemService.getDefaultItem();
            }
            else if (block.getType() == Material.ENDER_CHEST) {
                item = itemService.getLegendaryItem();
            }

            ItemStack itemStack = new ItemStack(item.getMaterial(), item.getAmount());
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(item.getName());
            itemStack.setItemMeta(meta);

            player.getInventory().addItem(itemStack);
            log.info("Added item %s to inventory of player %s".formatted(item, player.getName()));

            despawnChest(block);
        }
    }

    private void despawnChest(Block block) {
        log.warning("Despawning chest at position %s %s %s".formatted(block.getX(), block.getY(), block.getZ()));
        block.setType(Material.AIR);
    }


}

