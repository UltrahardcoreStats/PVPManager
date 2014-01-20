package com.ttaylorr.uhc.pvp.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class CombatTagEvent extends Event {
    private final Player player;

    public CombatTagEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
