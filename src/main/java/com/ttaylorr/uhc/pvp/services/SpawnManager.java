package com.ttaylorr.uhc.pvp.services;

import com.ttaylorr.uhc.pvp.util.Persistent;
import com.ttaylorr.uhc.pvp.services.interfaces.SpawnChooser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface SpawnManager extends List<Location>, Persistent {
    public Location respawn(Player p);
    public void setSpawnChooser(SpawnChooser chooser);
    public SpawnChooser getSpawnChooser();
}
