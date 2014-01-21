package com.ttaylorr.uhc.pvp.core.gamemodes;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.util.Continuation;
import com.ttaylorr.uhc.pvp.util.Message;
import nl.dykam.dev.spector.Spector;
import org.bukkit.entity.Player;

public class AdminGameMode extends GameMode {
    public AdminGameMode(PVPManagerPlugin plugin, Spector spector) {
        super(plugin, spector, "admin");
    }

    @Override
    protected void onEnter(Player p) {
        Message.success(p, "You joined admin mode");
    }

    @Override
    protected void onExit(Player p, Continuation continuation) {
        immediateExit(p);
        continuation.success();
    }

    @Override
    protected void onImmediateExit(Player p) {
        Message.success(p, "You left admin mode");
    }
}
