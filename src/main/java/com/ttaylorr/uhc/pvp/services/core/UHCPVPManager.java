package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.CommandListener;
import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.events.PlayerTaggedEvent;
import com.ttaylorr.uhc.pvp.services.*;
import com.ttaylorr.uhc.pvp.services.interfaces.PVPUtility;
import com.ttaylorr.uhc.pvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UHCPVPManager extends UHCGameModeBase implements PVPManager, Feature, Listener, CommandListener {
    SpawnManager spawnManager;
    PVPRestrictionManager pvpRestrictionManager;
    CombatTagger combatTagger;
    List<PVPUtility> utilityList;
    Map<Player, Continuation> exitters;
    Command[] commands;


    public UHCPVPManager(PVPManagerPlugin plugin) {
        super(plugin);

        commands = new Command[] {
                new AddSpawnCommand(),
                new RemoveSpawnCommand(),
                new RespawnSpawnCommand(),
        };
    }

    @Override
    public boolean onEnable() {
        if (!Bukkit.getServicesManager().isProvidedFor(SpawnManager.class))
            return false;
        if (!Bukkit.getServicesManager().isProvidedFor(PVPRestrictionManager.class))
            return false;
        if (!Bukkit.getServicesManager().isProvidedFor(CombatTagger.class))
            return false;
        spawnManager = Bukkit.getServicesManager().getRegistration(SpawnManager.class).getProvider();
        pvpRestrictionManager = Bukkit.getServicesManager().getRegistration(PVPRestrictionManager.class).getProvider();
        combatTagger = Bukkit.getServicesManager().getRegistration(CombatTagger.class).getProvider();

        utilityList = new ArrayList<>();
        for (RegisteredServiceProvider<PVPUtility> provider : Bukkit.getServicesManager().getRegistrations(PVPUtility.class)) {
            utilityList.add(provider.getProvider());
        }

        Bukkit.getPluginManager().registerEvents(this, getPlugin());

        return true;
    }

    @Override
    public void onDisable() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onEnter(Player p) {
        for (PVPUtility utility : utilityList)
            utility.subscribe(p);
        respawn(p);
    }

    @Override
    protected void onExit(Player p, Continuation continuation) {
        if(combatTagger.isTagged(p))
            continuation.failure();
        immediateExit(p);
        continuation = new ContinuationCounter(continuation, p, 5, "%d seconds left...", "Exiting PVP!");
        continuation.success();
    }

    @Override
    protected void onImmediateExit(Player p) {
        for (PVPUtility utility : utilityList)
            utility.unsubscribe(p);
    }

    @EventHandler
    private void onPlayerTagged(PlayerTaggedEvent pte) {
        if(!isInGameMode(pte.getPlayer()))
            return;
        if(pte.getService() != combatTagger)
            return;

        if(exitters.containsKey(pte.getPlayer())) {
            exitters.remove(pte.getPlayer()).failure();
        }
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        if(!isInGameMode(event.getEntity()))
            return;
        List<ItemStack> drops = event.getDrops();
        Iterator<ItemStack> dropIterator = drops.iterator();
        while (dropIterator.hasNext()) {
            if(dropIterator.next().getType() != Material.POTION || dropIterator.next().getType() != Material.GOLD_NUGGET)
                dropIterator.remove();
        }
        drops.add(new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1));
        KitLoader.clear(event.getEntity().getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        if(!isInGameMode(event.getPlayer()))
            return;
        Kit kit = getPlugin().getKits().getKit("pvp_default");
        if(null != kit)
            kit.apply(event.getPlayer(), true);
        else {
            KitLoader.clear(event.getPlayer().getInventory());
            getPlugin().getLogger().warning("Kit not found! pvp_default");
        }
        event.setRespawnLocation(spawnManager.getSpawn(event.getPlayer()));
    }

    private void respawn(Player player) {
        Kit kit = getPlugin().getKits().getKit("pvp_default");
        if(null != kit)
            kit.apply(player, true);
        else {
            KitLoader.clear(player.getInventory());
            getPlugin().getLogger().warning("Kit not found! pvp_default");
        }
        spawnManager.respawn(player);
    }

    @Override
    public Command[] getCommands() {
        return commands;
    }
    private class AddSpawnCommand extends PVPManagerCommand implements CommandExecutor {

        public AddSpawnCommand() {
            super(null, "pvp:addspawn");
            setExecutor(this);
        }
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if(!Checker.isPlayer(commandSender))
                return true;
            Player player = (Player)commandSender;
            spawnManager.add(player.getLocation());
            spawnManager.save();
            getPlugin().saveConfig();
            return false;
        }

    }
    private class RemoveSpawnCommand extends PVPManagerCommand implements CommandExecutor {

        public RemoveSpawnCommand() {
            super(null, "pvp:delspawn");
            setExecutor(this);
        }
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if(!Checker.isPlayer(commandSender))
                return true;
            Player player = (Player)commandSender;
            float range = 0.1f;
            if(strings.length > 1) {
                return false;
            }
            if(strings.length == 1) {
                range = Float.parseFloat(strings[0]);
            }

            Iterator<Location> locationIterator = spawnManager.iterator();
            int count = 0;
            while (locationIterator.hasNext()) {
                if(locationIterator.next().distanceSquared(player.getLocation()) < range * range) {
                    locationIterator.remove();
                    count++;
                }
            }
            if(count > 0) {
                Message.success(player, "Spawns removed: "  + count);
            } else {
                Message.warn(player, "Spawns removed: " + count);
            }
            getPlugin().saveConfig();
            return true;
        }

    }
    private class RespawnSpawnCommand extends PVPManagerCommand implements CommandExecutor {

        public RespawnSpawnCommand() {
            super(null, "pvp:respawn", "respawn");
            setExecutor(this);
        }
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if(!Checker.isPlayer(commandSender))
                return true;
            Player player = (Player)commandSender;
            respawn(player);
            return true;
        }

    }
}
