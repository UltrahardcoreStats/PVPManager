package com.ttaylorr.uhc.pvp.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    Player player;
    PlayerDataManager dataManager;
    Map<Class<?>, Object> dataMap;

    public PlayerData(PlayerDataManager dataManager, Player player) {
        this.player = player;
        this.dataManager = dataManager;
        dataMap = new HashMap<>();
    }

    public Player getPlayer() {
        return player;
    }

    public <T> T get(Class<T> klass) {
        if(!dataMap.containsKey(klass)) {
            init(klass);
        }
        return klass.cast(dataMap.get(klass));
    }

    public <T> void set(T data) {
        dataMap.put(data.getClass(), data);
    }

    public <T> void init(Class<T> klass) {
        PlayerDataManager.Factory<T> factory = dataManager.getFactory(klass);
        T value;
        try {
            value = factory.construct(player);
        } catch (Exception ex) {
            value = null;
            Bukkit.getLogger().severe("An exception occurred while initializing player data: ");
            ex.printStackTrace();
        }
        dataMap.put(klass, value);
    }
}
