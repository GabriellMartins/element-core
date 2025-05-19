package dev.github.gabrielmartins.module;

import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/*
 * Manages god mode state for players.
 */
@UtilityClass
public class GodManager {

    private static final Set<UUID> active = new HashSet<>();

    public boolean isActive(UUID uuid) {
        return active.contains(uuid);
    }

    public boolean toggle(UUID uuid) {
        if (active.contains(uuid)) {
            active.remove(uuid);
            return false;
        } else {
            active.add(uuid);
            return true;
        }
    }

    public void remove(UUID uuid) {
        active.remove(uuid);
    }
}
