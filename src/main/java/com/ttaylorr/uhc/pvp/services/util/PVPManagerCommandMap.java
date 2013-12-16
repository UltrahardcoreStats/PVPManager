package com.ttaylorr.uhc.pvp.services.util;

import org.bukkit.Bukkit;
import org.bukkit.command.*;

public class PVPManagerCommandMap extends SimpleCommandMap {
    public PVPManagerCommandMap() {
        super(Bukkit.getServer());
    }

    @Override
    public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
        String[] split = commandLine.split(" ", 2);
        String rawCommand = split[0];
        String[] args = split[1].split(" ");
        Command command = getCommand(rawCommand);
        return command.execute(sender, rawCommand, args);
    }
}
