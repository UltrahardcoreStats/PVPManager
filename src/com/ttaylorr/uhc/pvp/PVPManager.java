package com.ttaylorr.uhc.pvp;

import com.ttaylorr.uhc.pvp.services.*;
import com.ttaylorr.uhc.pvp.services.core.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PVPManager extends JavaPlugin {

	private static PVPManager instance;
	List<Feature> features;

	public static PVPManager get() {
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
		features = new ArrayList<Feature>();
		registerDefault(CombatTagger.class, new UHCCombatTagger());
		registerDefault(LobbyManager.class, new UHCLobbyManager());
		registerDefault(PVPRestrictionManager.class, new UHCPVPRestrictionManager());
		registerDefault(SpawnManager.class, new UHCSpawnManager());
		registerDefault(com.ttaylorr.uhc.pvp.services.PVPManager.class, new UHCPVPManager()); // Depends
																								// on
																								// SpawnManager,
																								// PVPRestrictionManager
																								// and
																								// CombatTagger
		registerDefault(UserManager.class, new UHCUserManager()); // Depends on
																	// PVPManager,
																	// LobbyManager

	}

	<T> void registerDefault(Class<T> type, T service) {
		Bukkit.getServicesManager().register(type, service, this, ServicePriority.Lowest);
		if (service instanceof Feature) features.add((Feature) service);
	}
}
