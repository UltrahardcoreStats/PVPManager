package com.ttaylorr.uhc.pvp.features;

import org.bukkit.event.Listener;

public class PVPFeature implements Listener {

	String name;
	String description;
	boolean enabled;
	
	public PVPFeature(String name, String description) {
		this.name = name;
		this.description = description;
		this.enabled = true;
	}
	
	public PVPFeature(String name, String description, boolean enabled) {
		this.name = name;
		this.description = description;
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public PVPFeature clone() {
		return new PVPFeature(this.name, this.description, this.enabled);
	}
	
}
