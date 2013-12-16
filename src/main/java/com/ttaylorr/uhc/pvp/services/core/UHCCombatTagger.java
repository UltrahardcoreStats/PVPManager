package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.services.CombatTagger;
import org.bukkit.entity.Player;

public class UHCCombatTagger implements CombatTagger, Feature {
    @Override
    public boolean onEnable() {
        //To change body of implemented methods use File | Settings | File Templates.
        return false;
    }

    public void onDisable() {
        // TODO Auto-generated method stub
    }

    public void tag(Player player) {
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
