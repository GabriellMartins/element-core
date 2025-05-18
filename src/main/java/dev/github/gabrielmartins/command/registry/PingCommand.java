package dev.github.gabrielmartins.command.registry;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import org.bukkit.command.CommandSender;

/*
 * Command: /ping
 * Sends a predefined message from the messages.yml (ping-usage)
 */
@CommandInfo(names = "ping")
public class PingCommand {

    public void execute(CommandSender sender, String[] args) {
        /*
         * Gets the messages config instance
         */
        var message = Engine.getEngine().getMessages();

        /*
         * Sends the message from messages.yml with key "ping-usage"
         */
        sender.sendMessage(message.getString("ping-usage"));
    }
}
