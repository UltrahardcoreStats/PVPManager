package com.ttaylorr.uhc.pvp.util;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class KitLoader {
    Map<String, Kit> kits;
    public KitLoader(ConfigurationSection config) {
        reload(config);
    }

    public void reload(ConfigurationSection config) {
        throw new NotImplementedException();
    }

    public Kit getKit(String name) {
        throw new NotImplementedException();
    }
}
