package dev.github.gabrielmartins.command.registry.inventory;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Command to open your own or another player's EnderChest.
 * Usage:
 * - /ec                 → opens your own EnderChest
 * - /ec <player>        → opens another player's EnderChest (if online)
 */
@CommandInfo(names = "ec")
public class EnderchestCommand {

    public void execute(CommandSender sender, String[] args) {
        var message = Engine.getEngine().getMessages();

        /*
         * Only players can use this command
         */
        if (!(sender instanceof Player)) {
            sender.sendMessage(message.getString("enderchest-check-player"));
            return;
        }

        var player = (Player) sender;

        /*
         * No args: open own EnderChest
         */
        if (args.length == 0) {
            player.openInventory(player.getEnderChest());
            player.sendMessage(message.getString("enderchest-opened-self"));
            return;
        }

        /*
         * Try to find the target player
         */
        var target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(message.getString("enderchest-player-not-found"));
            return;
        }

        /*
         * Check permission to open other's EnderChest
         */
        if (!player.hasPermission("essencecore.command.enderchest.others")) {
            player.sendMessage(message.getString("enderchest-no-permission-others"));
            return;
        }

        /*
         * Open target's EnderChest
         */
        player.openInventory(target.getEnderChest());
        player.sendMessage(message.getString("enderchest-opened-other")
                .replace("{player}", target.getName()));
    }
}
