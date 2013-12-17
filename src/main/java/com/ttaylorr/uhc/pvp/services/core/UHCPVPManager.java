package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

public class UHCPVPManager extends UHCGameModeBase implements PVPManager, Feature {
    SpawnManager spawnManager;
    PVPRestrictionManager pvpRestrictionManager;
    CombatTagger combatTagger;
    List<PVPUtility> utilityList;

    public UHCPVPManager(PVPManagerPlugin plugin) {
        super(plugin);
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

        return true;

        // TODO: Initialize the rest. Add listeners to track players.
    }

    @Override
    public void onDisable() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onEnter(Player p) {
        for (PVPUtility utility : utilityList)
            utility.subscribe(p);
    }

    @Override
    protected boolean onExit(Player p) {
        for (PVPUtility utility : utilityList)
            utility.unsubscribe(p);
        return true;
    }
}
