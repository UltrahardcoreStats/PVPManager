package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.LobbyManager;
import com.ttaylorr.uhc.pvp.services.PVPManager;
import com.ttaylorr.uhc.pvp.services.UserManager;
import com.ttaylorr.uhc.pvp.services.core.usermanager.Listeners;
import com.ttaylorr.uhc.pvp.services.util.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UHCUserManager extends UHCServiceBase implements UserManager, Feature {
    private final PlayerDataManager dataManager;
    private PVPManager pvpManager;
    private LobbyManager lobbyManager;

    private Listeners listener;

    public UHCUserManager(PVPManagerPlugin plugin) {
        super(plugin);
        dataManager = getPlugin().getDataManager();
    }

    @Override
    public boolean onEnable() {
        pvpManager = (PVPManager) Bukkit.getServicesManager().getRegistration(PVPManager.class);
        lobbyManager = (LobbyManager) Bukkit.getServicesManager().getRegistration(LobbyManager.class);

        listener = new Listeners(this);
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        return true;
    }

    public void onDisable() {
        // TODO Auto-generated method stub
    }

    public void subscribe(Player player) {
        getUserData(player).subscribed = true;
    }

    public void unsubscribe(Player player) {
        getUserData(player).subscribed = false;
    }

    private UserData getUserData(Player player) {
        return dataManager.get(player, UserData.class);
    }

    static class UserData {
        public boolean subscribed;
    }
}
