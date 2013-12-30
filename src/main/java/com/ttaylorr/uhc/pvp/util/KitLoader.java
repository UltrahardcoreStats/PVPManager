package com.ttaylorr.uhc.pvp.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.util.HashMap;
import java.util.Map;

public class KitLoader {
    Map<String, Kit> kits;

    public KitLoader(ConfigurationSection config) {
        kits = new HashMap<>();
        reload(config);
    }

    public void reload(ConfigurationSection config) {
        for(Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
            kits.put(entry.getKey(), new Kit((MemorySection) entry.getValue()));
        }
    }

    public Kit getKit(String name) {
        return kits.get(name);
    }
}
