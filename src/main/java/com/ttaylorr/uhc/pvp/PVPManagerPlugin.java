package com.ttaylorr.uhc.pvp;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.ttaylorr.uhc.pvp.services.*;
import com.ttaylorr.uhc.pvp.services.core.*;
import com.ttaylorr.uhc.pvp.services.core.UHCUserManager;
import com.ttaylorr.uhc.pvp.services.util.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PVPManagerPlugin extends JavaPlugin {

    private static PVPManagerPlugin instance;
    List<Feature> features;
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
        dataManager = new PlayerDataManager();
        features = new ArrayList<>();
        registerDefault(CombatTagger.class, new UHCCombatTagger());
        registerDefault(LobbyManager.class, new UHCLobbyManager());
        registerDefault(PVPRestrictionManager.class, new UHCPVPRestrictionManager());
        registerDefault(SpawnManager.class, new UHCSpawnManager());
        // Depends on SpawnManager, PVPRestrictionManager and CombatTagger
        registerDefault(com.ttaylorr.uhc.pvp.services.PVPManager.class, new UHCPVPManager());
        // Depends on PVPManagerPlugin, LobbyManager
        registerDefault(UserManager.class, new UHCUserManager(this));

        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            WorldGuardPlugin worldGuard = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin("WorldGuard");
            World world = Bukkit.getWorld("world");
            if (world != null && worldGuard.getRegionManager(world).hasRegion("hardcoded-regionname")) {
                registerDefault(PVPUtility.class, new UHCMagicWall("world", "hardcoded-regionname"));
            }
        }
        for(Feature feature : features)
            feature.onEnable();
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }

    <T> void registerDefault(Class<T> type, T service) {
        Bukkit.getServicesManager().register(type, service, this, ServicePriority.Lowest);
        if (service instanceof Feature) features.add((Feature) service);
    }
}
