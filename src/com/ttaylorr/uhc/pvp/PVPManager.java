package com.ttaylorr.uhc.pvp;

import org.bukkit.plugin.java.JavaPlugin;

public class PVPManager extends JavaPlugin {

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
	}

}
