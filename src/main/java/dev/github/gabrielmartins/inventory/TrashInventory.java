package dev.github.gabrielmartins.inventory;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.utils.item.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;

public class TrashInventory {

    public static final Set<Integer> PROTECTED_SLOTS = new HashSet<>();

    public static void open(Player player) {
        int rows = Engine.getEngine().getConfig().getInt("inventories.trash.size", 3);
        var title = Engine.getEngine().getConfig().getString("inventories.trash.title", "§cTrash Bin");

        int size = rows * 9;
        var inventory = Bukkit.createInventory(player, size, title);

        PROTECTED_SLOTS.clear();

        int lavaSlot = 4;

        for (int slot = 0; slot < size; slot++) {
            if (isBorder(slot, rows)) {
                inventory.setItem(slot, new ItemCreator(Material.GRAY_STAINED_GLASS_PANE)
                        .setName(" ")
                        .build());
                PROTECTED_SLOTS.add(slot);
            }
        }

        inventory.setItem(lavaSlot, new ItemCreator(Material.LAVA_BUCKET)
                .setName("§cTrash Everything Here")
                .setLore(
                        "§7Put any unwanted items here.",
                        "§7They will be §cpermanently deleted§7",
                        "§7when you close this menu."
                )
                .build());
        PROTECTED_SLOTS.add(lavaSlot);

        player.openInventory(inventory);
    }

    private static boolean isBorder(int slot, int rows) {
        int columns = 9;
        int lastRowStart = (rows - 1) * columns;
        return slot < columns ||
                slot >= lastRowStart ||
                slot % columns == 0 || slot % columns == 8;
    }
}
