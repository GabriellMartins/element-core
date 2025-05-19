package dev.github.gabrielmartins.command.registry.inventory;

import dev.github.gabrielmartins.Engine;
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
        var message = Engine.getEngine().getMessages();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(message.getString("enderchest-check-player"));
            return;
        }

        TrashInventory.open(player);
    }
}
