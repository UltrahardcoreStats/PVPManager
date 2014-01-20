package com.ttaylorr.uhc.pvp.core.usermanager;

import com.ttaylorr.uhc.pvp.core.gamemodes.GameMode;
import com.ttaylorr.uhc.pvp.core.UserManager;
import com.ttaylorr.uhc.pvp.util.Checker;
import com.ttaylorr.uhc.pvp.util.Continuation;
import com.ttaylorr.uhc.pvp.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.ttaylorr.uhc.pvp.util.Message.warn;

public class SwitchGameModeCommandExecutor implements CommandExecutor {
    private String alreadyInMessage;
    private UserManager userManager;
    private GameMode to;

    public SwitchGameModeCommandExecutor(UserManager userManager, String alreadyInMessage, GameMode to) {
        this.userManager = userManager;
        this.alreadyInMessage = alreadyInMessage;
        this.to = to;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!Checker.isPlayer(commandSender))
            return true;
        final Player player = (Player) commandSender;
        final UserManager.UserData userData = userManager.getUserData(player);
        if(userData.transitioning) {
            Message.warn(player, "You are already switching game mode!");
            return true;
        }
        if(userData.gameMode == to) {
            warn(player, alreadyInMessage);
            return true;
        }
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
        return false;
    }
}