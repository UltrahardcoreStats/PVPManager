package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.CombatTagger;
import com.ttaylorr.uhc.pvp.services.core.combattagger.CommandMatcher;
import com.ttaylorr.uhc.pvp.services.core.combattagger.Listeners;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class UHCCombatTagger implements CombatTagger, Feature, Listener {
    ConfigurationSection config;
    CommandMatcher commandMatcher;
    //Listeners listeners;
    @Override
    public boolean onEnable() {
        //To change body of implemented methods use File | Settings | File Templates.
        config = PVPManagerPlugin.get().getConfig().getConfigurationSection("combattag");
        commandMatcher = CommandMatcher.construct(config);
        //listeners = new Listeners(this, commandMatcher);
        //Bukkit.getPluginManager().registerEvents(listeners, PVPManagerPlugin.get());

        return true;
    }

    public void onDisable() {
        // TODO Auto-generated method stub
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
