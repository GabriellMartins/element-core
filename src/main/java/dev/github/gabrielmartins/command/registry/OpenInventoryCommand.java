package dev.github.gabrielmartins.command.registry;

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

        /*
         * Only players can use this command
         */
        if (!(sender instanceof Player)) {
            sender.sendMessage(messages.getString("openinv-check-player"));
            return;
        }

        var player = (Player) sender;

        /*
         * Usage: must include a target player name
         */
        if (args.length == 0) {
            player.sendMessage(messages.getString("openinv-usage"));
            return;
        }

        /*
         * Find the target player
         */
        var target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(messages.getString("openinv-player-not-found"));
            return;
        }

        /*
         * Open target's inventory (editable)
         */
        player.openInventory(target.getInventory());
        player.sendMessage(messages.getString("openinv-opened")
                .replace("{player}", target.getName()));
    }
}
