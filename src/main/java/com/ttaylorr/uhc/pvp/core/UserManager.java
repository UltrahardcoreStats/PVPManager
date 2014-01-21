package com.ttaylorr.uhc.pvp.core;

import com.ttaylorr.uhc.pvp.CommandListener;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.core.gamemodes.AdminGameMode;
import com.ttaylorr.uhc.pvp.core.gamemodes.GameMode;
import com.ttaylorr.uhc.pvp.core.gamemodes.PVPGameMode;
import com.ttaylorr.uhc.pvp.core.gamemodes.SpectatorGameMode;
import com.ttaylorr.uhc.pvp.core.usermanager.SwitchGameModeCommandExecutor;
import com.ttaylorr.uhc.pvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.*;


public class UserManager implements CommandListener {
    private final PlayerDataManager dataManager;

    private final Command[] commands;
    private PVPManagerPlugin plugin;
    private GameMode defaultGameMode;
    private List<GameMode> gameModes;

    public UserManager(PVPManagerPlugin plugin, GameMode defaultGameMode, GameMode... otherGameModes) {
        this.plugin = plugin;
        this.defaultGameMode = defaultGameMode;
        this.gameModes = new ArrayList<>();
        gameModes.add(defaultGameMode);
        gameModes.addAll(Arrays.asList(otherGameModes));
        dataManager = getPlugin().getDataManager();
        commands = new Command[]{
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
                        if (userData.gameMode instanceof PVPGameMode)
                            Message.message(player, "You are in PVP, to leave: " + ChatColor.UNDERLINE + "/pvp quit");
                        else if(userData.gameMode instanceof SpectatorGameMode)
                            Message.message(player, "You are in spectator mode, to leave: " + ChatColor.UNDERLINE + "/pvp spec-quit");
                        else if(userData.gameMode instanceof AdminGameMode)
                            Message.message(player, "You are in admin mode, to leave: " + ChatColor.UNDERLINE + "/pvp admin-quit");
                        else
                            Message.message(player, "You are in the lobby, to join: " + ChatColor.UNDERLINE + "/pvp join");
                    }
                    return true;
                }
            }, ""),
        };

        for(Player player : Bukkit.getOnlinePlayers())
            subscribe(player);
    }

    public void switchGameMode(final Player player, final GameMode to) {
        final UserData userData = getUserData(player);
        userData.transitioning = true;
        userData.gameMode.exit(player, new Continuation() {
            @Override
            public void success() {
                userData.transitioning = false;
                to.enter(player);
                userData.gameMode = to;
            }

            @Override
            public void failure() {
                userData.transitioning = false;
            }
        });
    }

    public void subscribe(Player player) {
        defaultGameMode.enter(player);
    }

    public void unsubscribe(Player player) {
        UserData userData = getUserData(player);
        userData.gameMode.exit(player, Continuation.empty());
    }

    public UserData getUserData(Player player) {
        return dataManager.get(player, UserData.class);
    }

    public void addTransition(String commandName, GameMode from, GameMode to, String alreadyInMessage, String wrongCurrentGameModeMessage) {
        SwitchGameModeCommandExecutor executor = new SwitchGameModeCommandExecutor(this, alreadyInMessage, wrongCurrentGameModeMessage, from, to);
        PVPManagerCommand command = new PVPManagerCommand(executor, commandName);
        plugin.registerCommand(command);
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
