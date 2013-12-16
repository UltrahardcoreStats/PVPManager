package com.ttaylorr.uhc.pvp.services.core.usermanager;

import com.ttaylorr.uhc.pvp.services.core.UHCUserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {
    private UHCUserManager userManager;

    public Listeners(UHCUserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent pje) {
        userManager.subscribe(pje.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent pqe) {
        userManager.unsubscribe(pqe.getPlayer());
    }
}
