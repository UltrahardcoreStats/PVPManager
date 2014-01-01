package com.ttaylorr.uhc.pvp.util;

import com.ttaylorr.uhc.pvp.CommandListener;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class KitLoader extends PersistentBase implements CommandListener {
    public static final ItemStack[] EMPTY_ARMOR = new ItemStack[4];
    Map<String, Kit> kits;
    private Command[] commands;

    public KitLoader(ConfigurationSection config) {
        kits = new HashMap<>();
        reload(config);

        commands = new Command[] {
            new KitSetCommand(),
            new KitApplyCommand(),
            new KitListCommand(),
        };
    }

    public void reload(ConfigurationSection config) {
        setConfig(config, "");
        load();
    }

    public Kit getKit(String name) {
        return kits.get(name);
    }

    @Override
    public void load(boolean clear) {
        for(Map.Entry<String, Object> entry : getConfig().getValues(false).entrySet()) {
            Kit kit = new Kit(getConfig(), entry.getKey());
            kits.put(entry.getKey(), kit);
            kit.load();
        }
    }

    @Override
    public void save(boolean clear) {
        for(Kit kit : kits.values())
            kit.save(clear);
    }

    @Override
    public Command[] getCommands() {
        return commands;
    }

    public static void clear(PlayerInventory playerInventory) {
        playerInventory.clear();
        playerInventory.setArmorContents(EMPTY_ARMOR);
    }


    private class KitSetCommand extends PVPManagerCommand implements CommandExecutor {
        public KitSetCommand() {
            super(null, "kit:set");
            setExecutor(this);
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if(!Checker.isPlayer(commandSender))
                return true;
            Player player = (Player) commandSender;

            if(strings.length != 1) {
                Message.failure(player, "No kit name is specified!  Please try: /pvp kit:set <name>");
                return true;
            }

            String name = strings[0];
            Kit kit;
            if (kits.containsKey(name)) {
                kit = kits.get(name);
            } else {
                kit = new Kit(getConfig(), name);
                kits.put(name, kit);
            }
            kit.read(player);
            kit.save();
            PVPManagerPlugin.get().saveConfig();
            return true;
        }
    }


    private class KitApplyCommand extends PVPManagerCommand implements CommandExecutor {
        public KitApplyCommand() {
            super(null, "kit:apply");
            setExecutor(this);
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if(!Checker.isPlayer(commandSender))
                return true;
            Player player = (Player) commandSender;

            if(strings.length != 1) {
                Message.failure(player, "No kit name is specified!  Please try: /pvp kit:apply <name>");
                return true;
            }

            String name = strings[0];
            if (!kits.containsKey(name)) {
                Message.failure(player, "Kit does not exist");
                return true;
            }
            Kit kit = kits.get(name);
            kit.apply(player, true);
            return true;
        }
    }


    private class KitListCommand extends PVPManagerCommand implements CommandExecutor {
        public KitListCommand() {
            super(null, "kit:list");
            setExecutor(this);
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if(!Checker.isPlayer(commandSender))
                return true;
            Player player = (Player) commandSender;
            Message.message(player, StringUtils.join(kits.keySet(), ", "));
            return true;
        }
    }
}
