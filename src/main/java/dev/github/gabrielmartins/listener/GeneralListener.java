package dev.github.gabrielmartins.listener;

import dev.github.gabrielmartins.module.GodManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/*
 * Handles general player events:
 * - Hides join/quit messages
 * - Applies god mode protection
 */
public final class GeneralListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Player player && GodManager.isActive(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Player player && GodManager.isActive(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
