package dev.github.gabrielmartins.api.cooldown.group;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.api.cooldown.Cooldown;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class CooldownGroup {

    @Getter
    private final String name;

    private final Map<UUID, Cooldown> cooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Long> warnedPlayers = new ConcurrentHashMap<>();

    public void setCooldown(UUID uuid, long milliseconds) {
        cooldowns.put(uuid, new Cooldown(System.currentTimeMillis(), milliseconds));
    }

    public boolean isActive(UUID uuid) {
        Cooldown cooldown = cooldowns.get(uuid);
        return cooldown != null && !cooldown.isExpired();
    }

    public long getRemaining(UUID uuid) {
        Cooldown cooldown = cooldowns.get(uuid);
        return (cooldown == null) ? 0 : cooldown.getRemaining();
    }

    private boolean recentlyWarned(UUID uuid) {
        Long lastWarned = warnedPlayers.get(uuid);
        return lastWarned != null && System.currentTimeMillis() - lastWarned < 1500;
    }

    private void markWarned(UUID uuid) {
        warnedPlayers.put(uuid, System.currentTimeMillis());
    }
    public void remove(UUID uuid) {
        cooldowns.remove(uuid);
    }

    public void runWithCooldown(Player player, long milliseconds, Runnable onFinish, Runnable onCooldown) {
        UUID uuid = player.getUniqueId();

        if (isActive(uuid)) {
            if (onCooldown != null && !recentlyWarned(uuid)) {
                onCooldown.run();
                markWarned(uuid);
            }
            return;
        }


        setCooldown(uuid, milliseconds);

        new BukkitRunnable() {
            int ticks = 0;

            final int originalLevel = player.getLevel();
            final float originalExp = player.getExp();

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (ticks >= 60) {
                    player.setLevel(originalLevel);
                    player.setExp(originalExp);

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    if (onFinish != null) onFinish.run();
                    cancel();
                    return;
                }

                player.setLevel(0);
                player.setExp(Math.min(1f, ticks / 60f));

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.3f, 1.5f);
                ticks += 5;
            }
        }.runTaskTimer(Engine.getEngine(), 0L, 5L);
    }
}