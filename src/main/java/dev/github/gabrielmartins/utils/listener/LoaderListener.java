package dev.github.gabrielmartins.utils.listener;

import dev.github.gabrielmartins.Engine;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Set;

public class LoaderListener {

    public LoaderListener load(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<? extends Listener>> listeners = reflections.getSubTypesOf(Listener.class);

        for (Class<? extends Listener> clazz : listeners) {
            if (Modifier.isAbstract(clazz.getModifiers())) continue;

            try {
                Listener instance;

                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                Constructor<?> target = null;

                for (Constructor<?> constructor : constructors) {
                    if (constructor.getParameterCount() == 0) {
                        target = constructor;
                        break;
                    }
                }

                if (target == null) continue;

                target.setAccessible(true);
                instance = (Listener) target.newInstance();

                Bukkit.getPluginManager().registerEvents(instance, Engine.getEngine());
            } catch (Exception ignored) {}
        }

        return this;
    }
}
