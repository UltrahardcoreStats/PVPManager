package com.ttaylorr.uhc.pvp.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import static com.google.common.base.Preconditions.checkNotNull;

public class Config {
    public static Location getLocation(ConfigurationSection config) {
        checkNotNull(config);
        Vector pos = config.getVector("position", new Vector(0, 0, 64));
        float yaw = (float)config.getDouble("yaw", 0);
        float pitch = (float)config.getDouble("pitch", 0);
        String world = config.getString("world", "world");
        return new Location(Bukkit.getWorld(world), pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
    }
    public static Location getLocation(ConfigurationSection config, String path) {
        return getLocation(getOrCreateSection(checkNotNull(config), checkNotNull(path)));
    }

    public static void setLocation(ConfigurationSection config, Location location) {
        checkNotNull(config);
        checkNotNull(location);
        config.set("position", location.toVector());
        config.set("yaw", location.getYaw());
        config.set("pitch", location.getPitch());
        config.set("world", location.getWorld().getName());
    }

    public static void setLocation(ConfigurationSection config, Location location, String path) {
        setLocation(getOrCreateSection(checkNotNull(config), checkNotNull(path)), checkNotNull(location));
    }

    public static ConfigurationSection getOrCreateSection(ConfigurationSection config, String path) {
        if(config.isConfigurationSection("path"))
            return config.getConfigurationSection("path");
        return config.createSection("path");
    }
}
