package com.ttaylorr.uhc.pvp;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.ttaylorr.uhc.pvp.services.*;
import com.ttaylorr.uhc.pvp.services.core.*;
import com.ttaylorr.uhc.pvp.services.core.UHCUserManager;
import com.ttaylorr.uhc.pvp.services.interfaces.PVPUtility;
import com.ttaylorr.uhc.pvp.util.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PVPManagerPlugin extends JavaPlugin {

    private static PVPManagerPlugin instance;
    List<Feature> features;
    CommandMap subCommands;
    PlayerDataManager dataManager;

    public static PVPManagerPlugin get() {
        return instance;
    }

    @Override
    public void onDisable() {
        for (Feature feature : features)
            feature.onDisable();
    }

    @Override
    public void onEnable() {
        instance = this;
        initializeConfig();
        Debug.init(this);
        dataManager = new PlayerDataManager();
        Bukkit.getPluginManager().registerEvents(dataManager, this);
        registerProviders();
        enableFeatures();

        subCommands.register("pvpmanager", new ReloadCommand());
    }

    private void enableFeatures() {
        for(Feature feature : features)
            feature.onEnable();
    }

    private void registerProviders() {
        features = new ArrayList<>();
        subCommands = new PVPManagerCommandMap();
        registerDefault(CombatTagger.class, new UHCCombatTagger());
        registerDefault(LobbyManager.class, new UHCLobbyManager(this));
        registerDefault(PVPRestrictionManager.class, new UHCPVPRestrictionManager());
        registerDefault(SpawnManager.class, new UHCSpawnManager());
        // Depends on SpawnManager, PVPRestrictionManager and CombatTagger
        registerDefault(PVPManager.class, new UHCPVPManager(this));
        // Depends on PVPManagerPlugin, LobbyManager
        registerDefault(UserManager.class, new UHCUserManager(this));

        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            WorldGuardPlugin worldGuard = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin("WorldGuard");
            World world = Bukkit.getWorld("world");
            String region = getConfig().getString("arena.region");
            if (world != null && worldGuard.getRegionManager(world).hasRegion(region)) {
                registerDefault(PVPUtility.class, new UHCMagicWall("world", region));
            }
        }
    }

    private void initializeConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return subCommands.dispatch(sender, args.length == 0 ? "" : StringUtils.join(args));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 0)
            return super.onTabComplete(sender, command, alias, args);
        return subCommands.tabComplete(sender, StringUtils.join(args, ' '));
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }

    <T> void registerDefault(Class<T> type, T service) {
        Bukkit.getServicesManager().register(type, service, this, ServicePriority.Lowest);
        if (service instanceof Feature) features.add((Feature) service);
        if(service instanceof CommandListener) {
            CommandListener commandListener = (CommandListener)service;
            for(Command command : commandListener.getCommands()) {
                subCommands.register("pvpmanager", command);
            }
        }
    }

    private class ReloadCommand extends PVPManagerCommand implements CommandExecutor {
        private ReloadCommand() {
            super(null, "reload");
            setExecutor(this);
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            reloadConfig();
            getConfig().options().copyDefaults(true);
            saveConfig();
            Message.success(commandSender, "Config reloaded");
            return true;
        }
    }
}
