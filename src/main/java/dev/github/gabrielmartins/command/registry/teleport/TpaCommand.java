package dev.github.gabrielmartins.command.registry.teleport;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.api.cooldown.api.CooldownAPI;
import dev.github.gabrielmartins.api.cooldown.format.CooldownFormatter;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CommandInfo(names = {"tpa"}, permission = {"core.command.tpa"})
public class TpaCommand {

    private static final Map<UUID, UUID> pendingRequests = new ConcurrentHashMap<>();

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage:");
            player.sendMessage("§e/tpa <player> §7- Send teleport request");
            player.sendMessage("§e/tpa accept §7- Accept a request");
            player.sendMessage("§e/tpa deny §7- Deny a request");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "accept" -> accept(player);
            case "deny" -> deny(player);
            default -> requestTeleport(player, args[0]);
        }
    }

    private void requestTeleport(Player sender, String targetName) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("§cPlayer not found.");
            return;
        }

        if (target.equals(sender)) {
            sender.sendMessage("§cYou can't send a teleport request to yourself.");
            return;
        }

        var group = CooldownAPI.getInstance().getGroup("tpa");

        group.runWithCooldown(sender, 10_000L, () -> {
            pendingRequests.put(target.getUniqueId(), sender.getUniqueId());

            sender.sendMessage("§aTeleport request sent to §e" + target.getName() + "§a.");
            target.sendMessage("§e" + sender.getName() + " §7wants to teleport to you.");
            target.sendMessage("§aType §e/tpa accept §ato accept or §e/tpa deny §ato deny. §7(30s)");

            Bukkit.getScheduler().runTaskLater(Engine.getEngine(), () -> {
                if (pendingRequests.remove(target.getUniqueId(), sender.getUniqueId())) {
                    sender.sendMessage("§cYour teleport request to §e" + target.getName() + " §cexpired.");
                }
            }, 20L * 30);

        }, () -> {
            long remaining = group.getRemaining(sender.getUniqueId());
            sender.sendMessage("§cYou must wait §e" + CooldownFormatter.format(remaining) + " §cbefore sending another teleport request.");
        });
    }

    private void accept(Player target) {
        UUID requesterId = pendingRequests.remove(target.getUniqueId());

        if (requesterId == null) {
            target.sendMessage("§cYou have no pending teleport requests.");
            return;
        }

        Player requester = Bukkit.getPlayer(requesterId);
        if (requester == null || !requester.isOnline()) {
            target.sendMessage("§cThe requester is no longer online.");
            return;
        }

        requester.teleport(target.getLocation());
        target.sendMessage("§aYou accepted §e" + requester.getName() + "§a's teleport request.");
        requester.sendMessage("§aYour teleport request to §e" + target.getName() + "§a was accepted.");
    }

    private void deny(Player target) {
        UUID requesterId = pendingRequests.remove(target.getUniqueId());

        if (requesterId == null) {
            target.sendMessage("§cYou have no pending teleport requests.");
            return;
        }

        Player requester = Bukkit.getPlayer(requesterId);
        if (requester != null && requester.isOnline()) {
            requester.sendMessage("§cYour teleport request to §e" + target.getName() + "§c was denied.");
        }

        target.sendMessage("§eYou denied the teleport request.");
    }
}
