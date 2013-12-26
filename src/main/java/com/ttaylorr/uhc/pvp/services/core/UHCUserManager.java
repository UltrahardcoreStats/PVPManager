package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.CommandListener;
import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.services.core.usermanager.SwitchGameModeCommandExecutor;
import com.ttaylorr.uhc.pvp.util.Continuation;
import com.ttaylorr.uhc.pvp.util.PVPManagerCommand;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.GameMode;
import com.ttaylorr.uhc.pvp.services.LobbyManager;
import com.ttaylorr.uhc.pvp.services.PVPManager;
import com.ttaylorr.uhc.pvp.services.UserManager;
import com.ttaylorr.uhc.pvp.services.core.usermanager.Listeners;
import com.ttaylorr.uhc.pvp.util.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;


public class UHCUserManager extends UHCServiceBase implements UserManager, Feature, CommandListener {
    private final PlayerDataManager dataManager;
    private final PVPManagerCommand quitCommand;
    private final PVPManagerCommand joinCommand;
    private PVPManager pvpManager;
    private LobbyManager lobbyManager;

    private final Command[] commands;

    private Listeners listeners;

    public UHCUserManager(PVPManagerPlugin plugin) {
        super(plugin);
        dataManager = getPlugin().getDataManager();
        joinCommand = new PVPManagerCommand(new SwitchGameModeCommandExecutor(this, "You are already in PVP", pvpManager), "join");
        quitCommand = new PVPManagerCommand(new SwitchGameModeCommandExecutor(this, "You are already not in PVP", lobbyManager), "quit");
        commands = new Command[]{
            joinCommand,
            quitCommand,
        };
    }

    @Override
    public boolean onEnable() {
        pvpManager = (PVPManager) Bukkit.getServicesManager().getRegistration(PVPManager.class).getProvider();
        lobbyManager = (LobbyManager) Bukkit.getServicesManager().getRegistration(LobbyManager.class).getProvider();

        joinCommand.setExecutor(new SwitchGameModeCommandExecutor(this, "You are already in PVP", pvpManager));
        quitCommand.setExecutor(new SwitchGameModeCommandExecutor(this, "You are already not in PVP", lobbyManager));

        listeners = new Listeners(this);
        Bukkit.getPluginManager().registerEvents(listeners, getPlugin());
        for(Player player : Bukkit.getOnlinePlayers())
            subscribe(player);
        return true;
    }

    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            UserData userData = getUserData(player);
            if(userData.isSubscribed())
                userData.gameMode.exit(player, Continuation.empty());
        }
    }

    public void subscribe(Player player) {
        lobbyManager.enter(player);
        getUserData(player).gameMode = lobbyManager;
    }

    public void unsubscribe(Player player) {
        UserData userData = getUserData(player);
        userData.gameMode.exit(player, Continuation.empty());
    }

    public UserData getUserData(Player player) {
        return dataManager.get(player, UserData.class);
    }

    @Override
    public Command[] getCommands() {
        return commands;
    }

    public static class UserData {
        public GameMode gameMode;

        public boolean isSubscribed() {
            return gameMode != null;
        }
    }
}
