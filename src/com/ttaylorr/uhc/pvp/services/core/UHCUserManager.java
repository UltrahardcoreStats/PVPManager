package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.services.LobbyManager;
import com.ttaylorr.uhc.pvp.services.PVPManager;
import com.ttaylorr.uhc.pvp.services.UserManager;
import org.bukkit.Bukkit;

public class UHCUserManager implements UserManager, Feature {
	
    PVPManager pvpManager;
	LobbyManager lobbyManager;

	public void onEnable() {
		pvpManager = (PVPManager) Bukkit.getServicesManager().getRegistration(PVPManager.class);
		lobbyManager = (LobbyManager) Bukkit.getServicesManager().getRegistration(LobbyManager.class);

		// TODO: Initialize user manager. Add command listener (pvp leave, pvp
		// join) etc.
	}

	public void onDisable() {
        // TODO Auto-generated method stub
	}
}
