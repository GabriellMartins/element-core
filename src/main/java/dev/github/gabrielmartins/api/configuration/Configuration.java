package dev.github.gabrielmartins.api.configuration;

import dev.github.gabrielmartins.Engine;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
public class Configuration {

    private final String name;
    private final File file;
    private YamlConfiguration config;

    public Configuration (String name) {
        this.name = name.endsWith(".yml") ? name : name + ".yml";
        this.file = new File(Engine.getEngine().getDataFolder(), this.name);
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            Engine.getEngine().getLogger().info("Creating default configuration: " + name);
            try {
                Engine.getEngine().saveResource(name, false);
            } catch (IllegalArgumentException e) {
                Engine.getEngine().getLogger().warning("Default resource not found in JAR: " + name);
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        InputStream defaultConfigStream = Engine.getEngine().getResource(name);
        if (defaultConfigStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultConfigStream, StandardCharsets.UTF_8));
            config.setDefaults(defaultConfig);
        }
    }


    public String getString(String path) {
        if (!config.contains(path)) {
            Engine.getEngine().getLogger().warning("Missing message key: " + path);
            return ChatColor.RED + "Message not found: " + path;
        }
        return ChatColor.translateAlternateColorCodes('&', config.getString(path));
    }
}
