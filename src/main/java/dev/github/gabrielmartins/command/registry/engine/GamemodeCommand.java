package dev.github.gabrielmartins.command.registry.engine;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Command: /gm or /gamemode
 * Changes the player's game mode (own or other player).
 */
@CommandInfo(
        names = {"gm", "gamemode"},
        permission = {"core.command.gamemode"}
)
public class GamemodeCommand {

    public void execute(CommandSender sender, String[] args) {
        var messages = Engine.getEngine().getMessages();

        /* Checks if sender has permission */
        if (!sender.hasPermission("core.command.gamemode")) {
            sender.sendMessage(messages.getString("no-permission"));
            return;
        }

        /* If no arguments: show usage */
        if (args.length == 0) {
            sender.sendMessage(messages.getString("gamemode-usage"));
            return;
        }

        /* Parses gamemode from first argument */
        var mode = switch (args[0].toLowerCase()) {
            case "0", "survival" -> GameMode.SURVIVAL;
            case "1", "creative" -> GameMode.CREATIVE;
            case "2", "adventure" -> GameMode.ADVENTURE;
            case "3", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };

        /* Invalid gamemode input */
        if (mode == null) {
            sender.sendMessage(messages.getString("gamemode-invalid").replace("{input}", args[0]));
            return;
        }

        /*
         * If second argument exists, use it as target player
         * Otherwise, default to the sender (if it's a player)
         */
        var target = args.length > 1 ? Bukkit.getPlayerExact(args[1]) :
                sender instanceof Player ? (Player) sender : null;

        /* Target player not found */
        if (target == null) {
            sender.sendMessage(messages.getString("gamemode-player-null"));
            return;
        }

        /* Set the gamemode */
        target.setGameMode(mode);

        /* Notify sender if changing another player's gamemode */
        if (sender != target) {
            sender.sendMessage(messages.getString("gamemode-set-other")
                    .replace("{target}", target.getName())
                    .replace("{mode}", mode.name().toLowerCase()));
        }

        /* Notify the target */
        target.sendMessage(messages.getString("gamemode-set-self")
                .replace("{mode}", mode.name().toLowerCase()));
    }
}
