package com.ttaylorr.uhc.pvp.core.interfaces;

import com.ttaylorr.uhc.pvp.util.Continuation;
import org.bukkit.entity.Player;

public interface GameMode {
    public void enter(Player p);

    public void exit(Player p, Continuation continuation);
    public void immediateExit(Player p);
    public Iterable<Player> getPlayers();
}
