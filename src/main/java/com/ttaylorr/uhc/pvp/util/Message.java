package com.ttaylorr.uhc.pvp.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Message {
    public static void message(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.AQUA + "[PVP]" + ChatColor.GRAY + " - " + ChatColor.RESET + message);
    }

    public static void message(CommandSender sender, ChatColor color, String message) {
        sender.sendMessage(ChatColor.AQUA + "[PVP]" + ChatColor.GRAY + " - " + ChatColor.RESET + colorize(color, message));
    }
    public static void warn(CommandSender sender, String message) {
        message(sender, ChatColor.GOLD, message);
    }
    public static void failure(CommandSender sender, String message) {
        message(sender, ChatColor.RED, message);
    }
    public static void success(CommandSender sender, String message) {
        message(sender, ChatColor.DARK_GREEN, message);
    }
    private static String colorize(ChatColor color, String message) {
        return color + message.replace(ChatColor.RESET.getChar(), color.getChar());
    }
}
