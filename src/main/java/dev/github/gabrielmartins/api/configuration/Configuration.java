package dev.github.gabrielmartins.api.configuration;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

@Getter
public class Configuration {

    private final Plugin plugin;
    private final String name;
    private final File file;
    private YamlConfiguration config;

    public Configuration(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name.endsWith(".yml") ? name : name + ".yml";
        this.file = new File(plugin.getDataFolder(), this.name);
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            try {
                plugin.saveResource(name, false);
            } catch (IllegalArgumentException ignored) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    public <T> T get(String path) {
        return (T) config.get(path);
    }

    public String getString(String path) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(path, "§cMessage not found: " + path));
    }

    public boolean contains(String path) {
        return config.contains(path);
    }
}
