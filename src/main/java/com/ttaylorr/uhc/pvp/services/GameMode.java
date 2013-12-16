package com.ttaylorr.uhc.pvp.services;

import org.bukkit.entity.Player;

public interface GameMode {
    public void enter(Player p);

    public boolean exit(Player p);
}
