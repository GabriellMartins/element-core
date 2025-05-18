package dev.github.gabrielmartins;

import dev.github.gabrielmartins.api.configuration.Configuration;
import dev.github.gabrielmartins.command.loader.CommandLoader;
import dev.github.gabrielmartins.utils.listener.LoaderListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Engine extends JavaPlugin {

    @Getter @Setter private static Engine engine;
    @Getter @Setter private static CommandLoader loader;
    @Getter private Configuration messages;

    @Override
    public void onLoad() {
        engine = this;
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        this.messages = new Configuration(this, "messages");

        loader = new CommandLoader();
        loader.load("dev.github.gabrielmartins.command.registry");
        new LoaderListener().load("dev.github.gabrielmartins.listener");
    }
}
