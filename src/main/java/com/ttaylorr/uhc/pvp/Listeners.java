package com.ttaylorr.uhc.pvp;

import com.ttaylorr.uhc.pvp.services.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class Listeners implements Listener {
    private PVPManagerPlugin plugin;
    private UserManager userManager;

    public Listeners(PVPManagerPlugin plugin, UserManager userManager) {
        this.plugin = plugin;
        this.userManager = userManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        userManager.subscribe(event.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        userManager.unsubscribe(event.getPlayer());
    }

    // Postpone loading until all worlds are loaded
    @EventHandler
    private void onWorldLoad(WorldLoadEvent event) {
        if(event.getWorld().getName().equals("uhc")) {
            plugin.initialize();
        }
    }
}