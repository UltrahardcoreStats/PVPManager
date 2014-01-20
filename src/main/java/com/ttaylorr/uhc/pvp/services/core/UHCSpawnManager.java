package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.interfaces.SpawnChooser;
import com.ttaylorr.uhc.pvp.util.Config;
import com.ttaylorr.uhc.pvp.util.serialization.SerializableLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UHCSpawnManager extends ArrayList<Location> {
    private SpawnChooser chooser;
    private ConfigurationSection config;
    private String configPath;

    public UHCSpawnManager(SpawnChooser chooser) {
        setSpawnChooser(chooser);
        if(config == null) {
            setConfig(PVPManagerPlugin.get().getConfig(), "arena.spawns");
        }
        load();
    }

    public Location respawn(Player p, SpawnChooser.Context context) {
        Location spawn = getSpawn(p, context);
        p.teleport(spawn);
        return spawn;
    }

    public Location getSpawn(Player p, SpawnChooser.Context context) {
        return chooser.choose(p, this, context);
    }

    public void setSpawnChooser(SpawnChooser chooser) {
        this.chooser = chooser;
    }

    public SpawnChooser getSpawnChooser() {
        return chooser;
    }

    public void setConfig(ConfigurationSection config, String path) {
        this.config = config;
        this.configPath = path;
    }

    public void load() {
        load(true);
    }

    public void load(boolean clear) {
        if(clear) clear();
        List<?> spawns = config.getList(configPath);
        for(Object spawnObject : spawns) {
            if (!(spawnObject instanceof SerializableLocation)) {
                continue;
            }

            add(((SerializableLocation)spawnObject).getLocation());
        }
    }

    public void save() {
        save(true);
    }

    @SuppressWarnings("unchecked")
    public void save(boolean clear) {
        List<Object> spawns = (List<Object>) config.getList(configPath);
        if(clear)
            spawns.clear();
        for(Location location : this) {
            spawns.add(Config.wrap(location));
        }
        config.set(configPath, spawns);
    }

}
