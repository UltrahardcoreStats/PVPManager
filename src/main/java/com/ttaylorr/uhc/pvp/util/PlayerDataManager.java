package com.ttaylorr.uhc.pvp.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
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
        for(Player player : Bukkit.getOnlinePlayers())
            subscribe(player);
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

    private void subscribe(Player player) {
        Debug.info("PlayerDataManager: "
                + ChatColor.GREEN + ChatColor.BOLD + "+ "
                + ChatColor.RESET + player.getName());
        playerDataMap.put(player, new PlayerData(this, player));
    }

    private void unsubscribe(Player player) {
        Debug.info("PlayerDataManager: "
                + ChatColor.RED + ChatColor.BOLD + "- "
                + ChatColor.RESET + player.getName());
        playerDataMap.remove(player);
    }

    /**
     * Create player data before the PlayerJoinEvent.
     * @param ple The event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerLogin(PlayerLoginEvent ple) {
        if(ple.getResult() != PlayerLoginEvent.Result.ALLOWED)
            return;
        Player player = ple.getPlayer();
        subscribe(player);
    }

    /**
     * Clean up player data when he logs out
     * @param pqe The event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent pqe) {
        Player player = pqe.getPlayer();
        unsubscribe(player);
    }

    @SuppressWarnings("unchecked")
    public <T> Factory<T> getFactory(Class<T> klass) {
        if(!factories.containsKey(klass)) {
            factories.put(klass, getDefaultFactory(klass));
        }
        return (Factory<T>) factories.get(klass);
    }

    private static <T> Factory<T> getDefaultFactory(Class<T> klass) {
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

    private static class DefaultFactory<T> implements Factory<T> {
        Constructor<T> constructor;

        private DefaultFactory(Constructor<T> constructor) {
            this.constructor = constructor;
        }

        @Override
        public T construct(Player player) throws Exception {
            Debug.info("PlayerDataManager: Constructed for player " + player.getName() + " " + constructor.getName());
            return constructor.newInstance();
        }
    }

    private static class DefaultFactoryWithPlayer<T> implements Factory<T> {
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
