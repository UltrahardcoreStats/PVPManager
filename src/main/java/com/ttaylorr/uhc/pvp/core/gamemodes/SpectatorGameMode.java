package com.ttaylorr.uhc.pvp.core.gamemodes;

import com.ttaylorr.uhc.pvp.CommandListener;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.util.*;
import nl.dykam.dev.Kit;
import nl.dykam.dev.KitAPI;
import nl.dykam.dev.reutil.ReUtil;
import nl.dykam.dev.spector.Spector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

public class SpectatorGameMode extends GameMode implements Listener, CommandListener {
    private Command[] commands;

    public SpectatorGameMode(PVPManagerPlugin plugin, Spector spector) {
        super(plugin, spector, "spectator");
        commands = new Command[] {
                new SetSpawnCommand(),
        };
        ReUtil.register(this, getPlugin());
    }

    @Override
    protected void onEnter(Player p) {
        p.teleport(getSpawn());
        p.setVelocity(new Vector());
        Kit kit = KitAPI.getManager().get("spectator");
        if(null != kit)
            kit.apply(p, true);
        else {
            InventoryUtils.clear(p.getInventory());
            getPlugin().getLogger().warning("Kit not found! spectator");
        }
        Message.success(p, "You joined spectator mode");
        Message.Broadcast.message(p.getDisplayName() + " became spectator");
    }

    @Override
    protected Continuation onExit(Player p, Continuation continuation) {
        immediateExit(p);
        continuation.success();
        return Continuation.empty();
    }

    @Override
    protected void onImmediateExit(Player p) {
        Message.success(p, "You left spectator mode");
        Message.Broadcast.message(p.getDisplayName() + " quit spectator");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        if(!isInGameMode(event.getPlayer()))
            return;
        event.setRespawnLocation(getSpawn());
        Kit kit = KitAPI.getManager().get("spectator");
        if(null != kit)
            kit.apply(event.getPlayer(), true);
        else {
            InventoryUtils.clear(event.getPlayer().getInventory());
            getPlugin().getLogger().warning("Kit not found! spectator");
        }
    }

    private Location getSpawn() {
        return Config.getLocation(getConfig(), "spawn");
    }

    private void setSpawn(Location location) {
        Config.setLocation(getConfig(), location, "spawn");
        getPlugin().saveConfig();
    }

    private ConfigurationSection getConfig() {
        FileConfiguration config = getPlugin().getConfig();
        return Config.getOrCreateSection(config, "spectator");
    }

    @Override
    public Command[] getCommands() {
        return commands;
    }

    private class SetSpawnCommand extends PVPManagerCommand implements CommandExecutor {
        public SetSpawnCommand() {
            super(null, "spectator:setspawn");
            setExecutor(this);
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if(!Checker.isPlayer(commandSender))
                return true;

            Player player = (Player)commandSender;
            setSpawn(player.getLocation());
            return true;
        }
    }
}