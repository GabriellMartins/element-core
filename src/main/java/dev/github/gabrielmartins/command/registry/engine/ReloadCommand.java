package dev.github.gabrielmartins.command.registry.engine;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import org.bukkit.command.CommandSender;

/*
 * Command: /core reload
 * Reloads the plugin configuration from config.yml
 */
@CommandInfo(
        names = {"core", "essencecore"},
        permission = {"core.command.reload"}
)
public class ReloadCommand {

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("§cUsage: /core reload");
            return;
        }

        Engine.getEngine().reloadConfig();
        sender.sendMessage("§aConfiguration reloaded successfully.");
    }
}
