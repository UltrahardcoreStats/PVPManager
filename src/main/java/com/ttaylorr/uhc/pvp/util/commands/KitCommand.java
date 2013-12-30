package com.ttaylorr.uhc.pvp.util.commands;

import com.google.common.base.Preconditions;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {
    private PVPManagerPlugin plugin;

    public KitCommand(PVPManagerPlugin plugin) {
        Preconditions.checkArgument(plugin.isEnabled(), "Plugin not enabled!");
        this.plugin = Preconditions.checkNotNull(plugin, "Plugin");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            Message.failure(sender, "Arguments not provided!");
            return true;
        }

        switch (args[0].toUpperCase()) {
            case "SAVE":
                if (!sender.hasPermission("pvpmanager.kits.save")) {
                    Message.failure(sender, "You don't have permission.");
                    return true;
                }

                if (!(sender instanceof Player)) {
                    Message.failure(sender, "You must have an inventory to execute this command!");
                    return true;
                }

                if (args.length != 2) {
                    Message.failure(sender, "No kit name is specified!  Please try: /kit save <name>");
                    return true;
                }

                Message.success(sender, "yay");

                break;
            case "LOAD":
                if (!sender.hasPermission("pvpmanager.kits.load")) {
                    Message.failure(sender, "You don't have permission.");
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }
}
