package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.services.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

public class UHCPVPManager implements PVPManager, Feature {
    SpawnManager spawnManager;
    PVPRestrictionManager pvpRestrictionManager;
    CombatTagger combatTagger;
    List<PVPUtility> utilityList;

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

        utilityList = new ArrayList<PVPUtility>();
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
    public boolean enter(Player p) {
        // TODO Auto-generated method stub
        for (PVPUtility utility : utilityList)
            utility.subscribe(p);
        return false;
    }

    public boolean exit(Player p) {
        // TODO Auto-generated method stub
        for (PVPUtility utility : utilityList)
            utility.unsubscribe(p);
        return false;
    }

}
