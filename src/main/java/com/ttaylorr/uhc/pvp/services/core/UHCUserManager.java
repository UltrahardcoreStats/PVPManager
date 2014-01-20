package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.CommandListener;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.core.usermanager.SwitchGameModeCommandExecutor;
import com.ttaylorr.uhc.pvp.services.interfaces.GameMode;
import com.ttaylorr.uhc.pvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class UHCUserManager implements CommandListener {
    private final PlayerDataManager dataManager;
    private final PVPManagerCommand quitCommand;
    private final PVPManagerCommand joinCommand;
    private UHCPVPManager pvpManager;
    private UHCLobbyManager lobbyManager;

    private final Command[] commands;
    private PVPManagerPlugin plugin;

    public UHCUserManager(PVPManagerPlugin plugin, UHCPVPManager pvpManager, UHCLobbyManager lobbyManager) {
        this.plugin = plugin;
        this.pvpManager = pvpManager;
        this.lobbyManager = lobbyManager;
        dataManager = getPlugin().getDataManager();
        joinCommand = new PVPManagerCommand(new SwitchGameModeCommandExecutor(this, "You are already in PVP", pvpManager), "join");
        quitCommand = new PVPManagerCommand(new SwitchGameModeCommandExecutor(this, "You are already not in PVP", lobbyManager), "quit");
        commands = new Command[]{
            joinCommand,
            quitCommand,
            new PVPManagerCommand(new CommandExecutor() {
                @Override
                public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                    if(!Checker.isPlayer(commandSender))
                        return true;
                    final Player player = (Player) commandSender;
                    final UserData userData = getUserData(player);
                    if(!userData.isSubscribed()) {
                        Message.warn(player, "You are not in a gamemode. Please relog");
                    } else {
                        Message.message(player, "You are in "  + (userData.gameMode == UHCUserManager.this.pvpManager
                                ? "PVP, to leave: " + ChatColor.UNDERLINE + "/pvp quit"
                                : "the lobby, to join: " + ChatColor.UNDERLINE + "/pvp join"
                        ));
                    }
                    return true;
                }
            }, ""),
        };

        joinCommand.setExecutor(new SwitchGameModeCommandExecutor(this, "You are already in PVP", pvpManager));
        quitCommand.setExecutor(new SwitchGameModeCommandExecutor(this, "You are already not in PVP", lobbyManager));

        for(Player player : Bukkit.getOnlinePlayers())
            subscribe(player);
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

    public PVPManagerPlugin getPlugin() {
        return plugin;
    }

    public static class UserData {
        public GameMode gameMode;
        public boolean transitioning;

        public boolean isSubscribed() {
            return gameMode != null;
        }
    }
}
