package com.ttaylorr.uhc.pvp;

import com.ttaylorr.uhc.pvp.core.UserManager;
import com.ttaylorr.uhc.pvp.core.gamemodes.PVPGameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Listeners implements Listener {
    private PVPManagerPlugin plugin;
    private UserManager userManager;

    public Listeners(PVPManagerPlugin plugin, UserManager userManager) {
        this.plugin = plugin;
        this.userManager = userManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
        userManager.subscribe(event.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        userManager.unsubscribe(event.getPlayer());
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
            return;
        UserManager.UserData userData = userManager.getUserData(event.getPlayer());
        if(userData == null)
            return;
        if(!(userData.gameMode instanceof PVPGameMode))
            event.setCancelled(true);
    }

}