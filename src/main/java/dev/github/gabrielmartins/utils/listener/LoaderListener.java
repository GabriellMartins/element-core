package dev.github.gabrielmartins.utils.listener;

import dev.github.gabrielmartins.Engine;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.lang.reflect.Modifier;

public class LoaderListener {

    public LoaderListener load(String basePackage) {
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .acceptPackages(basePackage)
                .scan()) {

            for (Class<?> clazz : scanResult.getClassesImplementing(Listener.class.getName()).loadClasses()) {
                if (Modifier.isAbstract(clazz.getModifiers())) continue;
                if (!Listener.class.isAssignableFrom(clazz)) continue;

                try {
                    Listener instance = (Listener) clazz.getDeclaredConstructor().newInstance();
                    Bukkit.getPluginManager().registerEvents(instance, Engine.getEngine());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }
}
