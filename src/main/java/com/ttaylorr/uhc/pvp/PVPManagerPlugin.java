package com.ttaylorr.uhc.pvp;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.ttaylorr.uhc.pvp.core.*;
import com.ttaylorr.uhc.pvp.core.gamemodes.LobbyMode;
import com.ttaylorr.uhc.pvp.core.gamemodes.PVPGameMode;
import com.ttaylorr.uhc.pvp.core.interfaces.SpawnChooser;
import com.ttaylorr.uhc.pvp.util.*;
import com.ttaylorr.uhc.pvp.util.serialization.SerializableLocation;
import nl.dykam.dev.FileKitManager;
import nl.dykam.dev.KitManager;
import nl.dykam.dev.spector.Spector;
import nl.dykam.dev.spector.SpectorAPI;
import nl.dykam.dev.spector.SpectorShield;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PVPManagerPlugin extends JavaPlugin {

    private static PVPManagerPlugin instance;
    List<Persistent> persistencies;
    CommandMap subCommands;
    PlayerDataManager dataManager;
    Listeners listeners;
    private Spector lobbySpector;
    private Spector pvpSpector;
    private Spector spectatorSpector;
    private Spector adminSpector;
    private UserManager userManager;

    public static PVPManagerPlugin get() {
        return instance;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        initSerializables();
        dataManager = new PlayerDataManager();
//        if(Bukkit.getWorld("uhc") != null)
            initialize();
    }

    private void initSerializables() {
        SerializableLocation.init();
    }

    public void initialize() {
        instance = this;
        initializeConfig();
        Debug.init(this);
        Bukkit.getPluginManager().registerEvents(dataManager, this);
        initializeKits();
        initializeSpector();
        registerProviders();
        listeners = new Listeners(this, userManager);
        Bukkit.getPluginManager().registerEvents(listeners, this);

        subCommands.register("pvpmanager", new ReloadCommand());
    }

    private void initializeSpector() {
        lobbySpector = SpectorAPI.create(this, "lobby");
        pvpSpector = SpectorAPI.create(this, "pvp");
        spectatorSpector = SpectorAPI.create(this, "spectator");
        adminSpector = SpectorAPI.create(this, "admin");

        lobbySpector.show(pvpSpector);
        lobbySpector.show(spectatorSpector);
        lobbySpector.setShield(SpectorShield.noShield());

        pvpSpector.hide(lobbySpector);
        pvpSpector.hide(spectatorSpector);
        pvpSpector.setShield(SpectorShield.noShield());

        spectatorSpector.show(lobbySpector);
        spectatorSpector.show(pvpSpector);
        spectatorSpector.setShield(SpectorShield.ghost());

        adminSpector.showAll();
        adminSpector.hideForAll();
        adminSpector.setShield(SpectorShield.noShield().canPickup(false));
    }

    private void initializeKits() {
        FileKitManager kitManager = new FileKitManager(getDataFolder().toPath().resolve("kits.yml").toFile());
        Bukkit.getServicesManager().register(KitManager.class, kitManager, this, ServicePriority.Normal);
    }

    private void registerProviders() {
        persistencies = new ArrayList<>();
        subCommands = new PVPManagerCommandMap();
        CombatTagger combatTagger = new CombatTagger();
        registerDefault(combatTagger);
        LobbyMode lobbyMode = new LobbyMode(this, lobbySpector);
        registerDefault(lobbyMode);
        SpawnManager spawnManager = new SpawnManager(SpawnChooser.far());
        registerDefault(spawnManager);
        // Depends on SpawnManager, PVPRestrictionManager and CombatTagger
        PVPGameMode pvpGameMode = new PVPGameMode(this, pvpSpector, spawnManager, combatTagger);
        registerDefault(pvpGameMode);
        // Depends on PVPManagerPlugin, LobbyMode
        userManager = new UserManager(this, pvpGameMode, lobbyMode);
        registerDefault(userManager);

        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            WorldGuardPlugin worldGuard = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin("WorldGuard");
            World world = Bukkit.getWorld("world");
            String region = getConfig().getString("arena.region");
            if (world != null && worldGuard.getRegionManager(world).hasRegion(region)) {
                registerDefault(new MagicWall("world", region));
            }
        }
    }

    private void initializeConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        this.saveResource("kits.yml", false);
    }

    @Override
    public void saveConfig() {
//        if(persistencies != null) {
//            for(Persistent persistent : persistencies) {
//                persistent.save();
//            }
//        }
        super.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return subCommands.dispatch(sender, args.length == 0 ? "" : StringUtils.join(args, ' '));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 0)
            return super.onTabComplete(sender, command, alias, args);
        return subCommands.tabComplete(sender, StringUtils.join(args, ' '));
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }

    void registerDefault(Object service) {
        if (service instanceof Persistent) persistencies.add((Persistent) service);
        if(service instanceof CommandListener) {
            CommandListener commandListener = (CommandListener)service;
            for(Command command : commandListener.getCommands()) {
                subCommands.register("pvpmanager", command);
            }
        }
    }

    private class ReloadCommand extends PVPManagerCommand implements CommandExecutor {
        private ReloadCommand() {
            super(null, "reload");
            setExecutor(this);
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            reloadConfig();
            getConfig().options().copyDefaults(true);
            saveConfig();
            Message.success(commandSender, "Config reloaded");
            return true;
        }
    }
}
