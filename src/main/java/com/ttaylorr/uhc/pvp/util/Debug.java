package com.ttaylorr.uhc.pvp.util;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * High-performance-ish logging proxy.
 */
public class Debug {
    static Instance instance = new NullInstance();
    public static void info(String message) {
        instance.info(message);
    }

    public static void init(Plugin plugin) {
        instance = plugin.getConfig().getBoolean("debug", false) ? new ActualInstance(plugin) : new NullInstance();
    }

    private interface Instance {
        void info(String message);
    }
    private static class ActualInstance implements Instance {
        private Plugin plugin;
        public ActualInstance(Plugin plugin) {
            this.plugin = plugin;
            info("Debug logging enabled...");
        }
        @Override
        public void info(String message) {
            plugin.getLogger().info(ChatColor.stripColor(message));
        }
    }
    private static class NullInstance implements Instance {
        @Override
        public void info(String message) {

        }
    }
}
