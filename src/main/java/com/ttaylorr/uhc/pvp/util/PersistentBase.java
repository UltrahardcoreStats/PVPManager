package com.ttaylorr.uhc.pvp.util;

import org.bukkit.configuration.ConfigurationSection;

public abstract class PersistentBase implements Persistent {
    private ConfigurationSection config;
    private String configPath;

    public void setConfig(ConfigurationSection config, String path) {
        this.config = config;
        this.configPath = path;
    }

    public ConfigurationSection getConfig() {
        return config;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void load() {
        load(true);
    }

    public void save() {
        save(true);
    }
}
