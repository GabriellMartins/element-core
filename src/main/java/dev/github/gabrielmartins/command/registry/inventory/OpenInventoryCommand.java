package dev.github.gabrielmartins.command.registry.inventory;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Command: /openinv <player>
 * Opens and allows editing of another player's inventory
 */
@CommandInfo(names = "openinv", permission = "essencecore.command.openinv")
public class OpenInventoryCommand {

    public void execute(CommandSender sender, String[] args) {
        var messages = Engine.getEngine().getMessages();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getString("openinv-check-player"));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(messages.getString("openinv-usage"));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(messages.getString("openinv-player-not-found"));
            return;
        }

        player.openInventory(target.getInventory());

        player.sendMessage(messages.getString("openinv-opened")
                .replace("{player}", target.getName()));
    }
}
