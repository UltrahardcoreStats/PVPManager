package com.ttaylorr.uhc.pvp.core.combattagger;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.core.UserManager;
import org.bukkit.entity.Player;

import java.util.Set;

public class SimpleCombatTagger implements CombatTagger {
    private PVPManagerPlugin plugin;
    Set<Player> members;

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

    @Override
    public void subscribe(Player p) {
        members.add(p);
    }

    @Override
    public void unsubscribe(Player p) {
        members.remove(p);
    }
}
