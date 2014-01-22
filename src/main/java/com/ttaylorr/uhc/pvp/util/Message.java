package com.ttaylorr.uhc.pvp.util;

import org.bukkit.Bukkit;
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
        message(sender, ChatColor.GOLD, "\u26a0 " + ChatColor.RESET + message);
    }
    public static void failure(CommandSender sender, String message) {
        message(sender, ChatColor.RED, ChatColor.BOLD + "\u2717 " + ChatColor.RESET + message);
    }
    public static void success(CommandSender sender, String message) {
        message(sender, ChatColor.GREEN, ChatColor.BOLD + "\u2714 " + ChatColor.RESET + message);
    }
    private static String colorize(ChatColor color, String message) {
        return color + message.replace(ChatColor.RESET.toString(), color.toString());
    }

    public static class Broadcast {
        public static void message(String message) {
            Bukkit.broadcastMessage(ChatColor.AQUA + "[PVP]" + ChatColor.GRAY + " - " + ChatColor.RESET + message);
        }

        public static void message(ChatColor color, String message) {
            Bukkit.broadcastMessage(ChatColor.AQUA + "[PVP]" + ChatColor.GRAY + " - " + ChatColor.RESET + colorize(color, message));
        }
        public static void warn(String message) {
            message(ChatColor.GOLD, "⚠ " + ChatColor.RESET + message);
        }
        public static void failure(String message) {
            message(ChatColor.RED, ChatColor.BOLD + "✗ " + ChatColor.RESET + message);
        }
        public static void success(String message) {
            message(ChatColor.DARK_GREEN, ChatColor.BOLD + "✔ " + ChatColor.RESET + message);
        }
    }
}
