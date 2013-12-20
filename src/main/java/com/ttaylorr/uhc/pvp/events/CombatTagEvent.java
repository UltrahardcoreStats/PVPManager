package com.ttaylorr.uhc.pvp.events;

import com.ttaylorr.uhc.pvp.services.CombatTagger;
import org.bukkit.entity.Player;

public abstract class CombatTagEvent {
    private final Player player;
    private final CombatTagger service;

    public CombatTagEvent(Player player, CombatTagger service) {
        this.player = player;
        this.service = service;
    }

    public Player getPlayer() {
        return player;
    }

    public CombatTagger getService() {
        return service;
    }
}
