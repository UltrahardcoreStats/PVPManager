package com.ttaylorr.uhc.pvp.util;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        String[] args = split.length == 1 ? ArrayUtils.EMPTY_STRING_ARRAY : split[1].split(" ");
        Command command = getCommand(rawCommand);
        if (command != null) {
            command.execute(sender, rawCommand, args);
        } else {
            suggestCommand(sender, rawCommand);
        }
        return true;
    }

    private void suggestCommand(CommandSender sender, String rawCommand) {
        Command command = null;
        int minimal = Integer.MAX_VALUE;
        for(Command possibleCommand : getCommands()) {
            if(!possibleCommand.testPermissionSilent(sender))
                continue;
            int distance = DamerauLevenshtein.Compute(rawCommand, possibleCommand.getName(), 10);
            if(distance >= minimal)
                continue;
            command = possibleCommand;
            minimal = distance;
        }

        if(command != null) {
            Message.warn(sender, "Command not found. Did you mean " + ChatColor.WHITE + ChatColor.UNDERLINE + command.getName() + ChatColor.RESET + "?");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine) {
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
