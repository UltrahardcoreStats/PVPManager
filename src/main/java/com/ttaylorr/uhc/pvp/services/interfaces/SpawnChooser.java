package com.ttaylorr.uhc.pvp.services.interfaces;

import com.ttaylorr.uhc.pvp.services.SpawnManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class SpawnChooser {
    private static final RandomChooser randomChooser = new RandomChooser();

    public abstract Location choose(Player player, SpawnManager spawns);
    public static SpawnChooser random() {
        return randomChooser;
    }
    static class RandomChooser extends SpawnChooser {
        @Override
        public Location choose(Player player, SpawnManager spawns) {
            return spawns.get((int)(Math.random() * spawns.size()));
        }
    }
}
