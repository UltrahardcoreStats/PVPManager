package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.Feature;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.SpawnManager;
import com.ttaylorr.uhc.pvp.services.interfaces.SpawnChooser;
import com.ttaylorr.uhc.pvp.util.Config;
import com.ttaylorr.uhc.pvp.util.serialization.SerializableLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UHCSpawnManager extends ArrayList<Location> implements SpawnManager, Feature {
    private SpawnChooser chooser;
    private ConfigurationSection config;
    private String configPath;

    public UHCSpawnManager(SpawnChooser chooser) {
        setSpawnChooser(chooser);
    }

    @Override
    public boolean onEnable() {
        if(config == null) {
            setConfig(PVPManagerPlugin.get().getConfig(), "arena.spawns");
        }
        load();
        return true;
    }

    public void onDisable() {

    }

    public Location respawn(Player p) {
        Location spawn = getSpawn(p);
        p.teleport(spawn);
        return spawn;
    }

    @Override
    public Location getSpawn(Player p) {
        return chooser.choose(p, this);
    }

    @Override
    public void setSpawnChooser(SpawnChooser chooser) {
        this.chooser = chooser;
    }

    @Override
    public SpawnChooser getSpawnChooser() {
        return chooser;
    }

    @Override
    public void setConfig(ConfigurationSection config, String path) {
        this.config = config;
        this.configPath = path;
    }

    @Override
    public void load() {
        load(true);
    }

    @Override
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

    @Override
    public void save() {
        save(true);
    }

    @Override
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
