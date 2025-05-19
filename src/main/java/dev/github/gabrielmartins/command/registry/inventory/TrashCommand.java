package dev.github.gabrielmartins.command.registry.inventory;

import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import dev.github.gabrielmartins.inventory.TrashInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Command: /trash
 * Opens a disposable inventory where players can throw away items.
 * All items inside are permanently deleted on close.
 */
@CommandInfo(
        names = {"trash"},
        permission = {"core.command.trash"}
)
public class TrashCommand {

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return;
        }

        TrashInventory.open(player);
    }
}
