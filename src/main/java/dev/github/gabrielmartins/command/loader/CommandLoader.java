package dev.github.gabrielmartins.command.loader;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class CommandLoader {

    public void load(String basePackage) {
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(basePackage)
                .scan()) {

            scanResult.getClassesWithAnnotation(CommandInfo.class.getName())
                    .loadClasses()
                    .forEach(clazz -> {
                        try {
                            CommandInfo annotation = clazz.getAnnotation(CommandInfo.class);
                            if (annotation == null) return;

                            Object instance = clazz.getDeclaredConstructor().newInstance();
                            Method method = clazz.getDeclaredMethod("execute", CommandSender.class, String[].class);
                            method.setAccessible(true);

                            CommandExecutor executor = (sender, command, label, args) -> {
                                try {
                                    method.invoke(instance, sender, args);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return true;
                            };

                            for (String name : annotation.names()) {
                                PluginCommand command = createPluginCommand(name);
                                if (command == null) continue;

                                command.setExecutor(executor);
                                if (annotation.permission().length > 0)
                                    command.setPermission(annotation.permission()[0]);

                                registerCommand(name, command);
                                Bukkit.getLogger().info("§a[CommandLoader] Registered: /" + name);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    @SneakyThrows
    private PluginCommand createPluginCommand(String name) {
        Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        constructor.setAccessible(true);
        return constructor.newInstance(name, Engine.getEngine());
    }

    @SneakyThrows
    private void registerCommand(String name, PluginCommand command) {
        SimpleCommandMap commandMap = getCommandMap();
        if (commandMap == null) return;

        Map<String, Command> knownCommands = getKnownCommands(commandMap);

        knownCommands.remove(name);
        knownCommands.remove(Engine.getEngine().getName().toLowerCase() + ":" + name);

        commandMap.register(Engine.getEngine().getName(), command);
    }

    @SneakyThrows
    private SimpleCommandMap getCommandMap() {
        if (!(Bukkit.getPluginManager() instanceof SimplePluginManager spm)) return null;

        Field f = SimplePluginManager.class.getDeclaredField("commandMap");
        f.setAccessible(true);
        return (SimpleCommandMap) f.get(spm);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private Map<String, Command> getKnownCommands(SimpleCommandMap commandMap) {
        Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
        f.setAccessible(true);
        return (Map<String, Command>) f.get(commandMap);
    }
}
