package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;

public abstract class UHCServiceBase {
    private PVPManagerPlugin plugin;

    public UHCServiceBase(PVPManagerPlugin plugin) {
        this.plugin = plugin;
    }

    public PVPManagerPlugin getPlugin() {
        return plugin;
    }
}


