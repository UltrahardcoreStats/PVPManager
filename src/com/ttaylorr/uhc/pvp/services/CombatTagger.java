package com.ttaylorr.uhc.pvp.services;

import org.bukkit.entity.Player;

public interface CombatTagger {
	void tag(Player player);

	void untag(Player player);

	boolean isTagged(Player player);
}
