package com.ttaylorr.uhc.pvp.core;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.core.combattagger.CommandMatcher;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PVPCombatTagger implements CombatTagger {
    ConfigurationSection config;
    CommandMatcher commandMatcher;

    public PVPCombatTagger() {
        config = PVPManagerPlugin.get().getConfig().getConfigurationSection("combattag");
        commandMatcher = CommandMatcher.construct(config);
    }

    @Override
    public void tag(Player defender, Player attacker) {
        // TODO Auto-generated method stub
    }

    @Override
    public void untag(Player player) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isTagged(Player player) {
        // TODO Auto-generated method stub
        return false;
    }
}
