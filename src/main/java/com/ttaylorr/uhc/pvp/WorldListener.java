package com.ttaylorr.uhc.pvp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {
    private PVPManagerPlugin plugin;

    public WorldListener(PVPManagerPlugin plugin) {
        this.plugin = plugin;
    }
    // Postpone loading until all worlds are loaded
    @EventHandler
    private void onWorldLoad(WorldLoadEvent event) {
        if(event.getWorld().getName().equals("uhc")) {
            plugin.initialize();
        }
    }
}
