package dev.github.gabrielmartins.command.registry;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        names = {"gm", "gamemode"},
        permission = {"core.command.gamemode"}
)
public class GamemodeCommand {

    public void execute(CommandSender sender, String[] args) {
        var messages = Engine.getEngine().getMessages();

        if (!sender.hasPermission("core.command.gamemode")) {
            sender.sendMessage(messages.getString("no-permission"));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(messages.getString("gamemode-usage"));
            return;
        }

        var mode = switch (args[0].toLowerCase()) {
            case "0", "survival" -> GameMode.SURVIVAL;
            case "1", "creative" -> GameMode.CREATIVE;
            case "2", "adventure" -> GameMode.ADVENTURE;
            case "3", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };

        if (mode == null) {
            sender.sendMessage(messages.getString("gamemode-invalid").replace("{input}", args[0]));
            return;
        }

        var target = args.length > 1 ? Bukkit.getPlayerExact(args[1]) :
                sender instanceof Player ? (Player) sender : null;

        if (target == null) {
            sender.sendMessage(messages.getString("gamemode-player-null"));
            return;
        }

        target.setGameMode(mode);
        if (sender != target)
            sender.sendMessage(messages.getString("gamemode-set-other")
                    .replace("{target}", target.getName())
                    .replace("{mode}", mode.name().toLowerCase()));

        target.sendMessage(messages.getString("gamemode-set-self")
                .replace("{mode}", mode.name().toLowerCase()));
    }
}
