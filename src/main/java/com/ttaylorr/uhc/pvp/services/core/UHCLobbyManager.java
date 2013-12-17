package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.LobbyManager;
import org.bukkit.entity.Player;

public class UHCLobbyManager extends UHCGameModeBase implements LobbyManager, Feature {
    public UHCLobbyManager(PVPManagerPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onEnable() {
        //To change body of implemented methods use File | Settings | File Templates.
        return false;
    }

    public void onDisable() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onEnter(Player p) {

    }

    @Override
    protected boolean onExit(Player p) {
        return false;
    }

}
