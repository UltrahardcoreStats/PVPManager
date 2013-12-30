package com.ttaylorr.uhc.pvp.util.serialization;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

public class SerializableLocation implements ConfigurationSerializable {
    Location location;

    public SerializableLocation(Map<String, Object> data) {
        location = new Location(
                Bukkit.getWorld((String) data.get("world")),
                (double)data.get("x"), (double)data.get("y"), (double)data.get("z"),
                (float)(double)data.get("yaw"), (float)(double)data.get("pitch")
        );
    }

    public SerializableLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public Map<String, Object> serialize() {
        if(location == null)
            return null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("world", location.getWorld().getName());
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());
        map.put("yaw", location.getYaw());
        map.put("pitch", location.getPitch());
        return map;
    }

    public static void init() {
        ConfigurationSerialization.registerClass(SerializableLocation.class);
    }
    static {
        ConfigurationSerialization.registerClass(SerializableLocation.class);
    }
}
