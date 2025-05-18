package dev.github.gabrielmartins.api.configuration;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
            plugin.getLogger().info("Creating default configuration: " + name);
            try {
                plugin.saveResource(name, false);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Default resource not found in JAR: " + name);
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        InputStream defaultConfigStream = plugin.getResource(name);
        if (defaultConfigStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultConfigStream, StandardCharsets.UTF_8));
            config.setDefaults(defaultConfig);
        }
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
        if (!config.contains(path)) {
            plugin.getLogger().warning("Missing message key: " + path);
            return ChatColor.RED + "Message not found: " + path;
        }
        return ChatColor.translateAlternateColorCodes('&', config.getString(path));
    }

    public boolean contains(String path) {
        return config.contains(path);
    }
}
