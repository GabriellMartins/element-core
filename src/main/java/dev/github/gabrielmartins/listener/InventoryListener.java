package dev.github.gabrielmartins.listener;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.inventory.TrashInventory;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onTrashClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        var title = event.getView().getTitle();
        var expected = Engine.getEngine().getConfig().getString("inventories.trash.title", "§cTrash Bin");
        if (!title.equalsIgnoreCase(expected)) return;

        int slot = event.getRawSlot();

        if (slot < event.getInventory().getSize() && TrashInventory.PROTECTED_SLOTS.contains(slot)) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot move this item.");
        }
    }

    @EventHandler
    public void onTrashClose(InventoryCloseEvent event) {
        var player = (Player) event.getPlayer();
        var inventory = event.getInventory();

        var expected = Engine.getEngine().getConfig().getString("inventories.trash.title", "§cTrash Bin");
        if (!event.getView().getTitle().equalsIgnoreCase(expected)) return;

        for (int i = 0; i < inventory.getSize(); i++) {
            if (!TrashInventory.PROTECTED_SLOTS.contains(i)) {
                inventory.setItem(i, null);
            }
        }

        player.sendMessage("§cAll items in the trash have been deleted.");
        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
    }
}
