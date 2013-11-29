package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.services.LobbyManager;
import com.ttaylorr.uhc.pvp.services.PVPManager;
import com.ttaylorr.uhc.pvp.services.UserManager;
import org.bukkit.Bukkit;

public class UHCUserManager implements UserManager, Feature {
  PVPManager pvpManager;
  LobbyManager lobbyManager;
  @Override
  public void onEnable() {
    pvpManager = (PVPManager) Bukkit.getServicesManager().getRegistration(PVPManager.class);
    lobbyManager = (LobbyManager)Bukkit.getServicesManager().getRegistration(LobbyManager.class);

    // TODO: Initialize user manager. Add command listener (pvp leave, pvp join) etc.
  }

  @Override
  public void onDisable() {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
