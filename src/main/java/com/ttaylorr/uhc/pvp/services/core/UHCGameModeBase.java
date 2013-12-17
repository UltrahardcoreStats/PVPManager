package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.GameMode;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class UHCGameModeBase extends UHCServiceBase implements GameMode {
    private Set<Player> players;
    protected abstract void onEnter(Player p);
    protected abstract boolean onExit(Player p);

    protected UHCGameModeBase(PVPManagerPlugin plugin) {
        super(plugin);
        players = new HashSet<>();
    }

    @Override
    public void enter(Player player) {
        onEnter(player);
        players.add(player);
    }

    @Override
    public boolean exit(Player player) {
        if(!onExit(player)) return false;
        players.remove(player);
        return true;
    }
}
