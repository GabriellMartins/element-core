package dev.github.gabrielmartins.command.loader;

import dev.github.gabrielmartins.Engine;
import dev.github.gabrielmartins.command.loader.info.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class CommandLoader {

    public void load(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CommandInfo.class);

        for (Class<?> clazz : classes) {
            try {
                CommandInfo annotation = clazz.getAnnotation(CommandInfo.class);
                Object instance = clazz.getDeclaredConstructor().newInstance();

                Method method = clazz.getDeclaredMethod("execute", CommandSender.class, String[].class);
                method.setAccessible(true);

                CommandExecutor executor = (sender, command, label, args) -> {
                    try {
                        method.invoke(instance, sender, args);
                    } catch (Exception ignored) {}
                    return true;
                };

                for (String name : annotation.names()) {
                    var command = createPluginCommand(name);
                    if (command == null) continue;

                    command.setExecutor(executor);

                    if (annotation.permission().length > 0) {
                        command.setPermission(annotation.permission()[0]);
                    }

                    registerCommand(command);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private PluginCommand createPluginCommand(String name) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, Engine.getEngine());
        } catch (Exception ignored) {
            return null;
        }
    }

    private void registerCommand(PluginCommand command) {
        if (command == null) return;

        try {
            CommandMap commandMap = getCommandMap();
            Map<String, Command> knownCommands = getKnownCommands(commandMap);

            knownCommands.remove(command.getName());
            knownCommands.remove(Engine.getEngine().getName().toLowerCase() + ":" + command.getName());

            commandMap.register(Engine.getEngine().getName(), command);
        } catch (Exception ignored) {
        }
    }

    private CommandMap getCommandMap() {
        try {
            var pm = Bukkit.getPluginManager();
            Field f = pm.getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            return (CommandMap) f.get(pm);
        } catch (Exception ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Command> getKnownCommands(CommandMap commandMap) throws Exception {
        Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
        f.setAccessible(true);
        return (Map<String, Command>) f.get(commandMap);
    }
}
