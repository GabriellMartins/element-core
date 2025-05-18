package dev.github.gabrielmartins.api.cooldown.api;

import dev.github.gabrielmartins.api.cooldown.group.CooldownGroup;
import lombok.Getter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownAPI {

    @Getter
    private static final CooldownAPI instance = new CooldownAPI();

    private final Map<String, CooldownGroup> groups = new ConcurrentHashMap<>();

    public CooldownGroup getGroup(String name) {
        return groups.computeIfAbsent(name.toLowerCase(), k -> new CooldownGroup(name));
    }
}