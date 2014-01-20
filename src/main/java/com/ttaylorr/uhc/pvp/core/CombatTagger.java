package com.ttaylorr.uhc.pvp.core;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.core.combattagger.CommandMatcher;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CombatTagger implements Listener {
    ConfigurationSection config;
    CommandMatcher commandMatcher;

    public CombatTagger() {
        config = PVPManagerPlugin.get().getConfig().getConfigurationSection("combattag");
        commandMatcher = CommandMatcher.construct(config);
    }

    public void tag(Player defender, Player attacker) {
        // TODO Auto-generated method stub
    }

    public void untag(Player player) {
        // TODO Auto-generated method stub
    }

    public boolean isTagged(Player player) {
        // TODO Auto-generated method stub
        return false;
    }
}
