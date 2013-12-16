package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.services.LobbyManager;
import org.bukkit.entity.Player;

public class UHCLobbyManager implements LobbyManager, Feature {
    @Override
    public boolean onEnable() {
        //To change body of implemented methods use File | Settings | File Templates.
        return false;
    }

    public void onDisable() {
        // TODO Auto-generated method stub
    }

    public boolean enter(Player p) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean exit(Player p) {
        // TODO Auto-generated method stub
        return false;
    }

}
