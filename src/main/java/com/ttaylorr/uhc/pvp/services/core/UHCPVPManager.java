package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.CommandListener;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.events.PlayerTaggedEvent;
import com.ttaylorr.uhc.pvp.services.interfaces.GameMode;
import com.ttaylorr.uhc.pvp.services.interfaces.SpawnChooser;
import com.ttaylorr.uhc.pvp.util.*;
import nl.dykam.dev.Kit;
import nl.dykam.dev.KitAPI;
import nl.dykam.dev.spector.Spector;
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
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UHCPVPManager extends UHCGameModeBase implements Listener, CommandListener, GameMode {
    private final SpawnChooser.Context context;
    private final Spector pvpSpector;
    UHCSpawnManager spawnManager;
    UHCCombatTagger combatTagger;
    Map<Player, Continuation> exitters;
    Command[] commands;


    public UHCPVPManager(PVPManagerPlugin plugin, Spector pvpSpector, UHCSpawnManager spawnManager, UHCCombatTagger combatTagger) {
        super(plugin);
        this.pvpSpector = pvpSpector;
        this.spawnManager = spawnManager;
        this.combatTagger = combatTagger;

        commands = new Command[] {
                new AddSpawnCommand(),
                new RemoveSpawnCommand(),
                new RespawnSpawnCommand(),
        };
        context = new SpawnChooser.Context(this);

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    protected void onEnter(Player p) {
        respawn(p);
        Message.Broadcast.message(p.getDisplayName() + " joined the arena!");
        for(Player other : Bukkit.getOnlinePlayers()) {
            if(other.hasMetadata("vanished") && other.getMetadata("vanished").get(0).asBoolean())
                continue;

            if(isInGameMode(other)) {
                p.showPlayer(other);
                other.showPlayer(p);
            } else {
                p.hidePlayer(other);
                other.showPlayer(p);
            }
        }
    }

    @Override
    protected void onExit(final Player p, Continuation continuation) {
        if(combatTagger.isTagged(p))
            continuation.failure();
        continuation = new ContinuationCounter(new Continuation(continuation) {
            @Override
            public void success() {
                immediateExit(p);
                super.success();
            }
        }, p, 5, "%d seconds left...", "Exiting PVP!");
        continuation.success();
    }

    @Override
    protected void onImmediateExit(Player p) {
        Message.Broadcast.message(p.getDisplayName() + " left the arena!");
    }

    @EventHandler
    private void onPlayerTagged(PlayerTaggedEvent pte) {
        if(!isInGameMode(pte.getPlayer()))
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
        InventoryUtils.clear(event.getEntity().getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        if(!isInGameMode(event.getPlayer()))
            return;
        Kit kit = KitAPI.getManager().get("pvp_default");
        if(null != kit)
            kit.apply(event.getPlayer(), true);
        else {
            InventoryUtils.clear(event.getPlayer().getInventory());
            getPlugin().getLogger().warning("Kit not found! pvp_default");
        }
        event.setRespawnLocation(spawnManager.getSpawn(event.getPlayer(), context));
    }

    private void respawn(Player player) {
        Kit kit = KitAPI.getManager().get("pvp_default");
        if(null != kit)
            kit.apply(player, true);
        else {
            InventoryUtils.clear(player.getInventory());
            getPlugin().getLogger().warning("Kit not found! pvp_default");
        }
        spawnManager.respawn(player, context);
        //player.setWalkSpeed(0.2888889014720917f);
        player.setWalkSpeed(0.24f);
        //player.setWalkSpeed(0.2f);
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
