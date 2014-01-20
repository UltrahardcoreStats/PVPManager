package com.ttaylorr.uhc.pvp.services.core.combattagger;

import com.ttaylorr.uhc.pvp.services.CombatTagger;
import com.ttaylorr.uhc.pvp.util.Message;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {
    private final CombatTagger combatTagger;
    private final CommandMatcher commandMatcher;

    public Listeners(CombatTagger combatTagger, CommandMatcher commandMatcher) {
        this.combatTagger = combatTagger;
        this.commandMatcher = commandMatcher;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamage() <= 0)
            return;
        if(!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player))
            return;

        combatTagger.tag((Player) event.getEntity(), (Player) event.getDamager());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByArrow(EntityDamageByEntityEvent event) {
        if(event.getDamage() <= 0)
            return;
        if (!(event.getEntity() instanceof Player && (event.getDamager() instanceof Projectile)))
            return;
        Projectile projectile = (Projectile)event.getDamager();
        if(!(projectile.getShooter() instanceof Player))
            return;
        combatTagger.tag((Player) event.getEntity(), (Player) projectile.getShooter());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        combatTagger.untag(event.getEntity());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("pvpmanager.combattag.bypass") || !combatTagger.isTagged(player))
            return;
//        Well, that doesn't work..
//        player.setLastDamageCause(new EntityDamageByEntityEvent(player, player, EntityDamageEvent.DamageCause.MAGIC, 10000.0));
        player.damage(10000.0, player);
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent pcpe) {
        if(!combatTagger.isTagged(pcpe.getPlayer()))
            return;
        if(commandMatcher.isAllowed(pcpe.getMessage()))
            return;
        pcpe.setCancelled(true);
        Message.failure(pcpe.getPlayer(), "You can't execute this command while in PVP.");
    }
}
