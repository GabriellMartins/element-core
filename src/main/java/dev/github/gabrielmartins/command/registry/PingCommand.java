package dev.github.gabrielmartins.command.registry;

import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import org.bukkit.command.CommandSender;

@CommandInfo(names = "ping")
public class PingCommand {

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§aPong!");
    }
}
