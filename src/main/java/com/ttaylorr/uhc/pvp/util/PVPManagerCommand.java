package com.ttaylorr.uhc.pvp.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.List;

public class PVPManagerCommand extends Command {
    CommandExecutor executor;

    public PVPManagerCommand(CommandExecutor executor, String name) {
        this(executor, name, name);
    }

    public PVPManagerCommand(CommandExecutor executor, String name, String description, String usageMessage, List<String> aliases) {
        this(executor, name, name, description, usageMessage, aliases);
    }

    public PVPManagerCommand(CommandExecutor executor, String name, String permission) {
        super(name);
        initialize(executor, name, permission);
    }

    public PVPManagerCommand(CommandExecutor executor, String name, String permission, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        initialize(executor, name, permission);
    }

    private void initialize(CommandExecutor executor, String name, String permission) {
        this.executor = executor;
        setPermission("pvpmanager.command." + permission);
        setPermissionMessage("You are not allowed to run /pvp " + name);
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!testPermission(commandSender))
            return false;
        return executor.onCommand(commandSender, this, s, strings);
    }
}
