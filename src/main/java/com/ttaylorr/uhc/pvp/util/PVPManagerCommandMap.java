package com.ttaylorr.uhc.pvp.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PVPManagerCommandMap extends SimpleCommandMap {
    Pattern SPACE = Pattern.compile(" ", Pattern.LITERAL);
    public PVPManagerCommandMap() {
        super(Bukkit.getServer());
        knownCommands.clear();
    }

    @Override
    public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
        String[] split = commandLine.split(" ", 2);
        String rawCommand = split[0];
        String[] args = split[1].split(" ");
        Command command = getCommand(rawCommand);
        return command.execute(sender, rawCommand, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        new Exception().printStackTrace();
        int spaceIndex = cmdLine.indexOf(' ');
        if(spaceIndex == -1) {
            List<String> results = new ArrayList<>();
            for(Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
                String alias = commandEntry.getKey();
                Command command = commandEntry.getValue();
                if(!StringUtil.startsWithIgnoreCase(alias, cmdLine))
                    continue;
                if(!command.testPermissionSilent(sender))
                    continue;
                results.add(alias);
            }
            return results;
        } else {
            String commandName = cmdLine.substring(0, spaceIndex);
            Command target = getCommand(commandName);
            String argLine = cmdLine.substring(spaceIndex + 1, cmdLine.length());
            String[] args = SPACE.split(argLine, -1);
            return target.tabComplete(sender, commandName, args);
        }
    }
}
