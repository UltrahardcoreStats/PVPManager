package com.ttaylorr.uhc.pvp.core.gamemodes;

import com.ttaylorr.uhc.pvp.CommandListener;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.util.*;
import nl.dykam.dev.Kit;
import nl.dykam.dev.KitAPI;
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

public class LobbyGameMode extends GameMode implements Listener, CommandListener {
    private Command[] commands;

    public LobbyGameMode(PVPManagerPlugin plugin, Spector spector) {
        super(plugin, spector, "lobby");
        commands = new Command[] {
            new SetSpawnCommand(),
        };
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    protected void onEnter(Player p) {
        p.teleport(getSpawn());
        p.setVelocity(new Vector());
        Kit kit = KitAPI.getManager().get("lobby");
        if(null != kit)
            kit.apply(p, true);
        else {
            InventoryUtils.clear(p.getInventory());
            getPlugin().getLogger().warning("Kit not found! lobby");
        }
    }

    @Override
    protected Continuation onExit(Player p, Continuation continuation) {
        continuation.success();
        return Continuation.empty();
    }

    @Override
    protected void onImmediateExit(Player p) {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        if(!isInGameMode(event.getPlayer()))
            return;
        event.setRespawnLocation(getSpawn());
        Kit kit = KitAPI.getManager().get("lobby");
        if(null != kit)
            kit.apply(event.getPlayer(), true);
        else {
            InventoryUtils.clear(event.getPlayer().getInventory());
            getPlugin().getLogger().warning("Kit not found! lobby");
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
        return Config.getOrCreateSection(config, "lobby");
    }

    @Override
    public Command[] getCommands() {
        return commands;
    }

    private class SetSpawnCommand extends PVPManagerCommand implements CommandExecutor {
        public SetSpawnCommand() {
            super(null, "lobby:setspawn");
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
