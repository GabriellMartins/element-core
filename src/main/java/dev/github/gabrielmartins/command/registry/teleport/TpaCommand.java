package dev.github.gabrielmartins.command.registry.teleport;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.api.cooldown.api.CooldownAPI;
import dev.github.gabrielmartins.api.cooldown.format.CooldownFormatter;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Command to send teleport requests to other players.
 * Usage:
 * - /tpa <player>      → sends a teleport request
 * - /tpa accept        → accepts the pending request
 * - /tpa deny          → denies the pending request
 *
 * Features:
 * - Prevents self requests
 * - Cooldown of 10 seconds per request
 * - Timeout for unanswered requests (30 seconds)
 * - Friendly messages and formatting
 */
@CommandInfo(names = {"tpa"}, permission = {"core.command.tpa"})
public class TpaCommand {

    private static final Map<UUID, UUID> pendingRequests = new ConcurrentHashMap<>();

    public void execute(CommandSender sender, String[] args) {
        var messages = Engine.getEngine().getMessages();

        /*
         * Only players can use this command
         */
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getString("tpa.not-player"));
            return;
        }

        /*
         * No args: show usage
         */
        if (args.length == 0) {
            List<String> usage = messages.getConfig().getStringList("tpa.usage");
            usage.forEach(msg -> player.sendMessage(format(msg)));
            return;
        }

        /*
         * Handle subcommands
         */
        switch (args[0].toLowerCase()) {
            case "accept" -> accept(player);
            case "deny" -> deny(player);
            default -> requestTeleport(player, args[0]);
        }
    }

    /*
     * Handles teleport request from player to target
     */
    private void requestTeleport(Player sender, String targetName) {
        var messages = Engine.getEngine().getMessages();

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(messages.getString("tpa.not-found"));
            return;
        }

        if (target.equals(sender)) {
            sender.sendMessage(messages.getString("tpa.self"));
            return;
        }

        var group = CooldownAPI.getInstance().getGroup("tpa");

        group.runWithCooldown(sender, 10_000L, () -> {
            pendingRequests.put(target.getUniqueId(), sender.getUniqueId());

            sender.sendMessage(messages.getString("tpa.sent").replace("{player}", target.getName()));
            target.sendMessage(messages.getString("tpa.received").replace("{player}", sender.getName()));
            target.sendMessage(messages.getString("tpa.info"));

            // Remove request after 30 seconds if not accepted
            Bukkit.getScheduler().runTaskLater(Engine.getEngine(), () -> {
                if (pendingRequests.remove(target.getUniqueId(), sender.getUniqueId())) {
                    sender.sendMessage(messages.getString("tpa.expired").replace("{player}", target.getName()));
                }
            }, 20L * 30);

        }, () -> {
            long remaining = group.getRemaining(sender.getUniqueId());
            sender.sendMessage(messages.getString("tpa.cooldown")
                    .replace("{time}", CooldownFormatter.format(remaining)));
        });
    }

    /*
     * Accepts pending teleport request
     */
    private void accept(Player target) {
        var messages = Engine.getEngine().getMessages();
        UUID requesterId = pendingRequests.remove(target.getUniqueId());

        if (requesterId == null) {
            target.sendMessage(messages.getString("tpa.none"));
            return;
        }

        Player requester = Bukkit.getPlayer(requesterId);
        if (requester == null || !requester.isOnline()) {
            target.sendMessage(messages.getString("tpa.not-found"));
            return;
        }

        requester.teleport(target.getLocation());
        target.sendMessage(messages.getString("tpa.accept.sender").replace("{player}", requester.getName()));
        requester.sendMessage(messages.getString("tpa.accept.target").replace("{player}", target.getName()));
    }

    /*
     * Denies pending teleport request
     */
    private void deny(Player target) {
        var messages = Engine.getEngine().getMessages();
        UUID requesterId = pendingRequests.remove(target.getUniqueId());

        if (requesterId == null) {
            target.sendMessage(messages.getString("tpa.none"));
            return;
        }

        Player requester = Bukkit.getPlayer(requesterId);
        if (requester != null && requester.isOnline()) {
            requester.sendMessage(messages.getString("tpa.deny.target").replace("{player}", target.getName()));
        }

        target.sendMessage(messages.getString("tpa.deny.sender"));
    }

    /*
     * Utility: formats color codes
     */
    private String format(String message) {
        return message.replace("&", "§");
    }
}
