package com.ttaylorr.uhc.pvp.services.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {
    public static void message(CommandSender sender, String message) {
        sender.sendMessage("[PVP] " + message);
    }
    public static void warn(CommandSender sender, String message) {
        message(sender, ChatColor.GOLD + message);
    }
    public static void failure(CommandSender sender, String message) {
        message(sender, ChatColor.RED + message);
    }
    public static void success(CommandSender sender, String message) {
        message(sender, ChatColor.DARK_GREEN + message);
    }
}
