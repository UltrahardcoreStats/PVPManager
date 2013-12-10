package com.ttaylorr.uhc.pvp.services;

import org.bukkit.entity.Player;

/**
 * Represents a service to provide some sort of useful feature.
 */
public interface PVPUtility {
    void subscribe(Player player);

    void unsubscribe(Player player);
}
