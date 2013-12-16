package com.ttaylorr.uhc.pvp.services.core.usermanager;

import com.ttaylorr.uhc.pvp.services.GameMode;
import com.ttaylorr.uhc.pvp.services.core.UHCUserManager;
import com.ttaylorr.uhc.pvp.services.util.Checker;
import com.ttaylorr.uhc.pvp.services.util.PVPManagerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.ttaylorr.uhc.pvp.services.util.Message.warn;

public class SwitchGameModeCommandExecutor implements CommandExecutor {
    private String alreadyInMessage;
    private UHCUserManager userManager;
    private GameMode to;

    public SwitchGameModeCommandExecutor(UHCUserManager userManager, String name, String alreadyInMessage, GameMode to) {
        this.userManager = userManager;
        this.alreadyInMessage = alreadyInMessage;
        this.to = to;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!Checker.isPlayer(commandSender))
            return true;
        Player player = (Player) commandSender;
        UHCUserManager.UserData userData = userManager.getUserData(player);
        if(userData.gameMode.equals(to)) {
            warn(player, alreadyInMessage);
            return true;
        }
        if(userData.gameMode.exit(player)) {
            to.enter(player);
            userData.gameMode = to;
        }
    }
}
