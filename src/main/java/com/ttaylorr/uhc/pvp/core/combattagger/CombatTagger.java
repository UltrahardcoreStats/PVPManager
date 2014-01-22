package com.ttaylorr.uhc.pvp.core.combattagger;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface CombatTagger extends Listener {
    void tag(Player defender, Player attacker);

    void untag(Player player);

    boolean isTagged(Player player);

    void subscribe(Player p);
    void unsubscribe(Player p);
}
