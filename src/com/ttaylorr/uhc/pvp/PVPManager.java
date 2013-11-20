package com.ttaylorr.uhc.pvp;

import org.bukkit.plugin.java.JavaPlugin;

import com.ttaylorr.uhc.pvp.features.FeatureManager;
import com.ttaylorr.uhc.pvp.features.core.EnderpearlFeature;

public class PVPManager extends JavaPlugin {

	private FeatureManager manager;
	private static PVPManager instance;

	public static PVPManager get() {
		return instance;
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {
		instance = this;

		manager = new FeatureManager();

		saveDefaultConfig();

		setUpFeatures();
		setUpCommands();
	}

	// @formatter:off
	private void setUpFeatures() {
		manager.removeAll();

		manager.addFeature(new EnderpearlFeature(
				getConfig().getBoolean("features.enderpearl.enabled"),
				getConfig().getDouble("features.enderpearl.max-height"),
				getConfig().getBoolean("features.enderpearl.give-back")
		));
	}
	// @formatter:on

	private void setUpCommands() {

	}

}
