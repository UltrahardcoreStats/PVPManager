package com.ttaylorr.uhc.pvp.util.serialization;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("SerializableLocation")
public class SerializableLocation implements ConfigurationSerializable {
    Location location;
    Map<String, Object> data;

    public SerializableLocation(Map<String, Object> data) {
        this.data = data;
    }

    private void deserialize() {
        String worldName = (String) data.get("world");
        location = new Location(
                Preconditions.checkNotNull(Bukkit.getWorld(worldName), "No world found with name " + worldName),
                (double)data.get("x"), (double)data.get("y"), (double)data.get("z"),
                (float)(double)data.get("yaw"), (float)(double)data.get("pitch")
        );
    }

    public SerializableLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        if(location == null && data != null)
            deserialize();
        return location;
    }

    public void setLocation(Location location) {
        data = null;
        this.location = location;
    }

    @Override
    public Map<String, Object> serialize() {
        if(location == null)
            return data;

        if(data == null)
            data = new HashMap<>();
        else
            data.clear();

        World world = location.getWorld();
        Preconditions.checkNotNull(world, "Location does not have a valid world");
        data.put("world", world.getName());
        data.put("x", location.getX());
        data.put("y", location.getY());
        data.put("z", location.getZ());
        data.put("yaw", location.getYaw());
        data.put("pitch", location.getPitch());
        return data;
    }

    public static void init() {
        ConfigurationSerialization.registerClass(SerializableLocation.class);
    }
    static {
        ConfigurationSerialization.registerClass(SerializableLocation.class);
    }
}
