package com.ttaylorr.uhc.pvp.features.core;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.ttaylorr.uhc.pvp.features.PVPFeature;

public class EnderpearlFeature extends PVPFeature {

	private double MAX_HEIGHT;
	private boolean GIVE_BACK_PEARL;

	public EnderpearlFeature(boolean enabled, double maxHeight, boolean giveBack) {
		super("ENDERPERAL", "Limits the height of thrown enderpearls", enabled);
		
		this.MAX_HEIGHT = maxHeight;
		this.GIVE_BACK_PEARL = giveBack;
	}

	@EventHandler
	public void onEnderPearlThrow(PlayerTeleportEvent event) {
		if(event.getCause() != TeleportCause.ENDER_PEARL)
			return;
		
		if(event.getTo().getY() < MAX_HEIGHT)
			return;
		
		if(GIVE_BACK_PEARL) {
			event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
		}
		
		event.setCancelled(true);
	}
	
}
