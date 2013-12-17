package com.ttaylorr.uhc.pvp.services.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataManager implements Listener {
    private Map<Player, PlayerData> playerDataMap;
    private Map<Class<?>, Factory<?>> factories;
    public PlayerDataManager() {
        playerDataMap = new HashMap<>();
        factories = new HashMap<>();
    }
    public <T> T get(Player player, Class<T> klass) {
        PlayerData playerData = get(player);
        return playerData.get(klass);
    }
    public PlayerData get(Player player) {
        return playerDataMap.get(player);
    }
    public <T> void set(Player player, T data) {
        PlayerData playerData = playerDataMap.get(player);
        playerData.set(data);
    }
    public <T> void setFactory(Class<T> klass, Factory<T> factory) {
        factories.put(klass, factory);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent pje) {
        playerDataMap.put(pje.getPlayer(), new PlayerData(this, pje.getPlayer()));
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent pqe) {
        playerDataMap.remove(pqe.getPlayer());
    }

    @SuppressWarnings("unchecked")
    public <T> Factory<T> getFactory(Class<T> klass) {
        if(!factories.containsKey(klass)) {
            factories.put(klass, getDefaultFactory(klass));
        }
        return (Factory<T>) factories.get(klass);
    }

    private <T> Factory<T> getDefaultFactory(Class<T> klass) {
        Factory<T> defaultFactory = tryConstructFactory(klass);
        if(defaultFactory == null)
            defaultFactory = new NullFactory<>();
        return defaultFactory;
    }

    @SuppressWarnings("unchecked")
    public static <T> Factory<T> tryConstructFactory(Class<T> klass) {
        for(Constructor<T> constructor : (Constructor<T>[])klass.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if(parameterTypes.length == 0)
                return new DefaultFactory<>(constructor);
            else if(parameterTypes.length == 1 && parameterTypes[0].equals(Player.class))
                return new DefaultFactoryWithPlayer<>(constructor);
        }
        return new NullFactory<>();
    }

    static class DefaultFactory<T> implements Factory<T> {
        Constructor<T> constructor;

        private DefaultFactory(Constructor<T> constructor) {
            this.constructor = constructor;
        }

        @Override
        public T construct(Player player) throws Exception {
            return constructor.newInstance();
        }
    }

    static class DefaultFactoryWithPlayer<T> implements Factory<T> {
        Constructor<T> constructor;

        private DefaultFactoryWithPlayer(Constructor<T> constructor) {
            this.constructor = constructor;
        }

        @Override
        public T construct(Player player) throws Exception {
            return constructor.newInstance(player);
        }
    }

    static class NullFactory<T> implements Factory<T> {
        @Override
        public T construct(Player player) throws Exception {
            return null;
        }
    }

    static interface Factory<V> {
        V construct(Player player) throws Exception;
    }
}
