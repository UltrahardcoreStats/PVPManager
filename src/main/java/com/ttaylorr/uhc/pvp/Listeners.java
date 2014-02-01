package com.ttaylorr.uhc.pvp;

import com.ttaylorr.uhc.pvp.core.UserData;
import com.ttaylorr.uhc.pvp.core.UserManager;
import com.ttaylorr.uhc.pvp.core.gamemodes.PVPGameMode;
import com.ttaylorr.uhc.pvp.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;

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
        UserData userData = userManager.getUserData(event.getPlayer());
        if(userData == null)
            return;
        if(!(userData.gameMode instanceof PVPGameMode))
            event.setCancelled(true);
    }

    @EventHandler
    private void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntityType() == EntityType.ENDER_PEARL) {
            if (!(event.getEntity().getShooter() instanceof Player))
                return;
            Player shooter = (Player) event.getEntity().getShooter();
            UserData userData = userManager.getUserData(shooter);
            if (userData == null)
                return;
            if (userData.transitioning) {
                event.setCancelled(true);
                Message.warn(shooter, "You can't throw an enderpearl when quitting PVP.");
            }
        } else if(event.getEntityType() == EntityType.ARROW) {
            if (!(event.getEntity().getShooter() instanceof Player))
                return;
            Player shooter = (Player) event.getEntity().getShooter();
            UserData userData = userManager.getUserData(shooter);
            if (userData == null)
                return;
            if(!(userData.gameMode instanceof PVPGameMode))
                return;
            event.getEntity().setMetadata("InstantRemove", new FixedMetadataValue(plugin, null));
        }
    }
    @EventHandler
    private void onProjectileHit(final ProjectileHitEvent event) {
        if(event.getEntity().hasMetadata("InstantRemove")) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    event.getEntity().remove();
                }
            }, 2 * 20);
        }
    }
}