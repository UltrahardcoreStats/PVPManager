package com.ttaylorr.uhc.pvp.core;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import org.bukkit.entity.Player;

public class SimpleCombatTagger implements CombatTagger {
    private PVPManagerPlugin plugin;

    public SimpleCombatTagger(PVPManagerPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public void tag(Player defender, Player attacker) {
        tag(defender);
        tag(attacker);
    }

    private void tag(Player player) {
        UserManager.UserData userData = plugin.getUserManager().getUserData(player);
        if(userData.transitioning) {
            userData.transition.failure();
        }
    }

    @Override
    public void untag(Player player) {

    }

    @Override
    public boolean isTagged(Player player) {
        return false;
    }
}
