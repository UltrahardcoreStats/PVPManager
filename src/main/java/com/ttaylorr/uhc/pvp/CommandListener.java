package com.ttaylorr.uhc.pvp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;

public interface CommandListener extends CommandExecutor {
    Command[] getCommands();
}
