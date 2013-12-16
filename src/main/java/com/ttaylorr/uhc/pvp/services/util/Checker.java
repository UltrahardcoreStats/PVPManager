package com.ttaylorr.uhc.pvp.services.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Checker {
    public static boolean isPlayer(CommandSender sender) {
        if(sender instanceof Player)
            return true;
        Message.failure(sender, "You need to be a player to execute this command");
        return false;
    }
}
