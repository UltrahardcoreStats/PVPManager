package com.ttaylorr.uhc.pvp;

import com.ttaylorr.uhc.pvp.services.UserManager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {
    private UserManager userManager;

    public Listeners(UserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        userManager.subscribe(event.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        userManager.unsubscribe(event.getPlayer());
    }
}