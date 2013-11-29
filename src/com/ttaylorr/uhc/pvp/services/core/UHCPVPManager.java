package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.services.CombatTagger;
import com.ttaylorr.uhc.pvp.services.PVPRestrictionManager;
import com.ttaylorr.uhc.pvp.services.SpawnManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ttaylorr.uhc.pvp.services.PVPManager;

public class UHCPVPManager implements PVPManager, Feature {
  SpawnManager spawnManager;
  PVPRestrictionManager pvpRestrictionManager;
  CombatTagger combatTagger;
  @Override
  public void onEnable() {
    spawnManager = (SpawnManager)Bukkit.getServicesManager().getRegistration(SpawnManager.class);
    pvpRestrictionManager = (PVPRestrictionManager)Bukkit.getServicesManager().getRegistration(PVPRestrictionManager.class);
    combatTagger = (CombatTagger)Bukkit.getServicesManager().getRegistration(CombatTagger.class);

    // TODO: Initialize the rest. Add listeners to track players.
  }

  @Override
  public void onDisable() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
	public boolean enter(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exit(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

}
