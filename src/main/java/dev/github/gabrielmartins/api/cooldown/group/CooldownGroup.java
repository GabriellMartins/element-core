package dev.github.gabrielmartins.api.cooldown.group;

import dev.github.gabrielmartins.api.cooldown.Cooldown;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class CooldownGroup {

    @Getter
    private final String name;

    private final Map<UUID, Cooldown> cooldowns = new ConcurrentHashMap<>();

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

    public void remove(UUID uuid) {
        cooldowns.remove(uuid);
    }
}