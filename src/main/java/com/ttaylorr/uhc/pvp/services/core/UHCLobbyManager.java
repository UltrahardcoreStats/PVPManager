package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.CommandListener;
import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.LobbyManager;
import com.ttaylorr.uhc.pvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class UHCLobbyManager extends UHCGameModeBase implements LobbyManager, Feature, CommandListener {
    private Command[] commands;

    public UHCLobbyManager(PVPManagerPlugin plugin) {
        super(plugin);
        commands = new Command[] {
            new SetSpawnCommand(),
        };
    }

    @Override
    public boolean onEnable() {
        //To change body of implemented methods use File | Settings | File Templates.
        return false;
    }

    public void onDisable() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onEnter(Player p) {
        if(p.hasPermission("pvpmanager.join.teleport.bypass")) {
            Debug.info("Skipped player " + p.getName() + " when teleporting to lobby spawn.");
            return;
        }
        p.teleport(getSpawn());
        p.setVelocity(new Vector());
    }

    @Override
    protected void onExit(Player p, Continuation continuation) {
        continuation.success();
    }

    @Override
    protected void onImmediateExit(Player p) {

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
            super(null, "lobby:setspawn", "lobby.setspawn");
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
