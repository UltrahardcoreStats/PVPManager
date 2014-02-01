package com.ttaylorr.uhc.pvp.core;

import com.google.common.base.Function;
import com.ttaylorr.uhc.pvp.CommandListener;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.core.gamemodes.AdminGameMode;
import com.ttaylorr.uhc.pvp.core.gamemodes.GameMode;
import com.ttaylorr.uhc.pvp.core.gamemodes.PVPGameMode;
import com.ttaylorr.uhc.pvp.core.gamemodes.SpectatorGameMode;
import com.ttaylorr.uhc.pvp.util.Checker;
import com.ttaylorr.uhc.pvp.util.Continuation;
import com.ttaylorr.uhc.pvp.util.Message;
import com.ttaylorr.uhc.pvp.util.PVPManagerCommand;
import net.milkbowl.vault.permission.Permission;
import nl.dykam.dev.reutil.data.ComponentHandle;
import nl.dykam.dev.reutil.data.ComponentManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;


public class UserManager implements CommandListener {
    private final ComponentHandle<Player, UserData> dataHandle;
    private final Command[] commands;
    private PVPManagerPlugin plugin;
    private Function<Player, GameMode> defaultGameMode;
    private GameModeCollection gameModes;

    public UserManager(PVPManagerPlugin plugin, Function<Player, GameMode> defaultGameMode, GameMode... gameModes) {
        this.plugin = plugin;
        this.defaultGameMode = defaultGameMode;
        this.gameModes = new GameModeCollection(this, defaultGameMode);
        this.gameModes.add(Arrays.asList(gameModes));
        dataHandle = ComponentManager.get(plugin).get(UserData.class);
        commands = new Command[] {
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
                            Message.message(player, "You are in spectator mode, to leave: " + ChatColor.UNDERLINE + "/pvp quit");
                        else if(userData.gameMode instanceof AdminGameMode)
                            Message.message(player, "You are in admin mode, to leave: " + ChatColor.UNDERLINE + "/pvp quit");
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
        if(userData.transitioning) {
            userData.transition.failure();
        }
        userData.transitioning = true;
        userData.transition = userData.gameMode.exit(player, new Continuation() {
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
        Permission permission = plugin.getPermission();
        if(permission != null) {
            for (GameMode gameMode : gameModes.getGameModes()) {
                if(permission.playerInGroup(player, gameMode.getPermissionGroup()))
                    permission.playerRemoveGroup(player, gameMode.getPermissionGroup());
            }
        }
        gameModes.getDefaultGameMode(player).enter(player);
    }

    public void unsubscribe(Player player) {
        UserData userData = getUserData(player);
        if(userData == null || userData.gameMode == null)
            return;
        userData.gameMode.getSpector().unAssignTo(player);
        userData.gameMode.immediateExit(player);
    }

    public void unsubscribeAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            unsubscribe(player);
        }
    }

    public UserData getUserData(Player player) {
        return dataHandle.get(player);
    }

    @Override
    public Command[] getCommands() {
        return commands;
    }

    public PVPManagerPlugin getPlugin() {
        return plugin;
    }

    public GameModeCollection getGameModes() {
        return gameModes;
    }

}
