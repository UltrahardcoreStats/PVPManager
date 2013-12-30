package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.events.PlayerTaggedEvent;
import com.ttaylorr.uhc.pvp.services.*;
import com.ttaylorr.uhc.pvp.services.interfaces.PVPUtility;
import com.ttaylorr.uhc.pvp.util.Continuation;
import com.ttaylorr.uhc.pvp.util.ContinuationCounter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UHCPVPManager extends UHCGameModeBase implements PVPManager, Feature, Listener {
    SpawnManager spawnManager;
    PVPRestrictionManager pvpRestrictionManager;
    CombatTagger combatTagger;
    List<PVPUtility> utilityList;
    Map<Player, Continuation> exitters;


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
        if(pte.getService() != combatTagger)
            return;

        if(exitters.containsKey(pte.getPlayer())) {
            exitters.remove(pte.getPlayer()).failure();
        }
    }
}
