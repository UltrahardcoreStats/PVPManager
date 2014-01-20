package com.ttaylorr.uhc.pvp.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerTaggedEvent extends CombatTagEvent {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public PlayerTaggedEvent(Player player) {
        super(player);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
