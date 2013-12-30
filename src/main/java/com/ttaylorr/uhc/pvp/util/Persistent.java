package com.ttaylorr.uhc.pvp.util;

import org.bukkit.configuration.ConfigurationSection;

public interface Persistent {
    public void setConfig(ConfigurationSection config, String path);
    /**
     * Loads the current instants with data from @config
     */
    public void load();
    /**
     * Loads the current instants with data from @config
     * @param clear  Whether or not to clear the current data first (override)
     */
    public void load(boolean clear);
    /**
     * Saves the current instance to @config
     */
    public void save();
    /**
     * Saves the current instance to @config
     * @param clear  Whether or not to clear the current data first (override)
     */
    public void save(boolean clear);
}
