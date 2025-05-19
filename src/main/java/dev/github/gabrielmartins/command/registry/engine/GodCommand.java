package dev.github.gabrielmartins.command.registry.engine;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import dev.github.gabrielmartins.module.GodManager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Command: /god [player]
 * Toggles god mode for yourself or another player.
 * When enabled, the player becomes immune to damage and hunger.
 */
@CommandInfo(
        names = {"god"},
        permission = {"core.command.god"}
)
public class GodCommand {

    public void execute(CommandSender sender, String[] args) {
        var messages = Engine.getEngine().getMessages();

        Player target;

        /*
         * No arguments: apply god mode to the sender
         * Requires sender to be a player
         */
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(messages.getString("god-require-player"));
                return;
            }

            target = (Player) sender;
        } else {
            /*
             * Try to find the target player
             */
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(messages.getString("god-player-not-found"));
                return;
            }
        }

        /*
         * Toggle god mode for the target
         */
        var uuid = target.getUniqueId();
        var enabled = GodManager.toggle(uuid);

        /*
         * Send feedback messages
         */
        if (target.equals(sender)) {
            target.sendMessage(messages.getString(enabled ? "god-enabled-self" : "god-disabled-self"));
        } else {
            sender.sendMessage(messages.getString(enabled ? "god-enabled-other" : "god-disabled-other")
                    .replace("{player}", target.getName()));
            target.sendMessage(messages.getString(enabled ? "god-enabled-self" : "god-disabled-self"));
        }
    }
}
