package dev.github.gabrielmartins.command.registry;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.api.cooldown.api.CooldownAPI;
import dev.github.gabrielmartins.api.cooldown.format.CooldownFormatter;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Command to repair the item in hand.
 * If the player is in survival mode, a visual XP-based animation plays before the item is fixed.
 * The command has a cooldown defined via CooldownAPI.
 *
 * Usage:
 * - /fix               → repairs the item in the player's hand
 */
@CommandInfo(names = "fix")
public class FixCommand {

    public void execute(CommandSender sender, String[] args) {
        var messages = Engine.getEngine().getMessages();

        /*
         * Only players can use this command
         */
        if (!(sender instanceof Player)) {
            sender.sendMessage(messages.getString("fix-check-player"));
            return;
        }

        var player = (Player) sender;
        var item = player.getInventory().getItemInHand();

        /*
         * No item in hand
         */
        if (item == null || item.getType().isAir()) {
            player.sendMessage(messages.getString("fix-no-item"));
            return;
        }

        var group = CooldownAPI.getInstance().getGroup("fix");

        /*
         * Check if the player is on cooldown
         */
        if (group.isActive(player.getUniqueId())) {
            long remaining = group.getRemaining(player.getUniqueId());
            player.sendMessage(messages.getString("fix-cooldown")
                    .replace("{time}", CooldownFormatter.format(remaining)));
            return;
        }

        /*
         * Start repair effect and animation
         */
        player.sendMessage(messages.getString("fix-starting"));
        group.runWithCooldown(player, 10_000L, () -> {
            item.setDurability((short) 0);
            player.sendMessage(messages.getString("fix-success"));
        }, null);
    }
}
