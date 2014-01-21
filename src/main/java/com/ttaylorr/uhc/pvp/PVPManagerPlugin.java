package com.ttaylorr.uhc.pvp;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.ttaylorr.uhc.pvp.core.CombatTagger;
import com.ttaylorr.uhc.pvp.core.MagicWall;
import com.ttaylorr.uhc.pvp.core.SpawnManager;
import com.ttaylorr.uhc.pvp.core.UserManager;
import com.ttaylorr.uhc.pvp.core.gamemodes.AdminGameMode;
import com.ttaylorr.uhc.pvp.core.gamemodes.LobbyGameMode;
import com.ttaylorr.uhc.pvp.core.gamemodes.PVPGameMode;
import com.ttaylorr.uhc.pvp.core.gamemodes.SpectatorGameMode;
import com.ttaylorr.uhc.pvp.core.interfaces.SpawnChooser;
import com.ttaylorr.uhc.pvp.util.*;
import com.ttaylorr.uhc.pvp.util.serialization.SerializableLocation;
import net.milkbowl.vault.permission.Permission;
import nl.dykam.dev.FileKitManager;
import nl.dykam.dev.KitManager;
import nl.dykam.dev.spector.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    Permission permission;

    public static PVPManagerPlugin get() {
        return instance;
    }

    @Override
    public void onDisable() {
        userManager.unsubscribeAll();
    }

    @Override
    public void onEnable() {
        initSerializables();
        dataManager = new PlayerDataManager();
        setupPermission();
        initializeConfig();
        if(Bukkit.getWorld(getConfig().getString("world")) != null)
            initialize();
        else
            Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
    }

    private boolean setupPermission() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            return true;
        }
        return false;
    }

    private void initSerializables() {
        SerializableLocation.init();
    }

    public void initialize() {
        instance = this;
        Debug.init(this);
        Bukkit.getPluginManager().registerEvents(dataManager, this);
        initializeKits();
        initializeSpector();
        registerProviders();
        listeners = new Listeners(this, userManager);
        Bukkit.getPluginManager().registerEvents(listeners, this);

        registerCommand(new ReloadCommand());
        registerCommand(new ListCommand(), true);
    }

    private void initializeSpector() {
        lobbySpector = SpectorAPI.create(this, "lobby");
        pvpSpector = SpectorAPI.create(this, "pvp");
        spectatorSpector = SpectorAPI.create(this, "spectator");
        adminSpector = SpectorAPI.create(this, "admin");

        lobbySpector.show(pvpSpector);
        lobbySpector.show(spectatorSpector);
        lobbySpector.setShield(SpectorShield.noShield());
        lobbySpector.setSettings(SpectorSettings.spectator().canFly(false));

        pvpSpector.hide(lobbySpector);
        pvpSpector.hide(spectatorSpector);
        pvpSpector.setShield(SpectorShield.noShield());
        pvpSpector.setSettings(SpectorSettings.survival().walkSpeed(0.24f));

        spectatorSpector.show(lobbySpector);
        spectatorSpector.show(pvpSpector);
        spectatorSpector.setShield(SpectorShield.ghost().canChat(true));
        spectatorSpector.setSettings(SpectorSettings.spectator());
        spectatorSpector.addComponent(new TeamComponent(
                Bukkit.getScoreboardManager().getMainScoreboard(),
                ChatColor.BOLD + "◊ " + ChatColor.GRAY,
                TeamMode.Ghost));

        adminSpector.showAll();
        adminSpector.hideForAll();
        adminSpector.setShield(SpectorShield.noShield().canPickup(false));
        adminSpector.setSettings(SpectorSettings.spectator().gameMode(GameMode.CREATIVE));
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
        LobbyGameMode lobbyMode = new LobbyGameMode(this, lobbySpector);
        registerDefault(lobbyMode);
        SpawnManager spawnManager = new SpawnManager(SpawnChooser.far());
        registerDefault(spawnManager);
        // Depends on SpawnManager, PVPRestrictionManager and CombatTagger
        PVPGameMode pvpGameMode = new PVPGameMode(this, pvpSpector, spawnManager, combatTagger);
        registerDefault(pvpGameMode);

        AdminGameMode adminGameMode = new AdminGameMode(this, adminSpector);
        registerDefault(adminGameMode);
        SpectatorGameMode spectatorGameMode = new SpectatorGameMode(this, spectatorSpector);
        registerDefault(spectatorGameMode);

        // Depends on PVPManagerPlugin, LobbyGameMode
        userManager = new UserManager(this, lobbyMode, pvpGameMode, spectatorGameMode);
        userManager.addTransition("join", lobbyMode, pvpGameMode, "You are already in PVP", "You can only join from the lobby");
        userManager.addTransition("quit", pvpGameMode, lobbyMode, "You are already in the lobby", "You can only quit when in PVP");
        userManager.addTransition("admin-join", null, adminGameMode, "You are already in admin mode", "");
        userManager.addTransition("admin-quit", adminGameMode, lobbyMode, "You are already in lobby mode", "You are not in admin mode");
        userManager.addTransition("spec-join", lobbyMode, spectatorGameMode, "You are already in spectator mode", "You can only join spectator mode from lobby");
        userManager.addTransition("spec-quit", spectatorGameMode, lobbyMode, "You are already in the lobby", "You are not in spectator mode");
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
                registerCommand(command);
            }
        }
    }

    public boolean registerCommand(Command command) {
        return registerCommand(command, false);
    }

    public boolean registerCommand(Command command, boolean root) {
        if(root && command instanceof PVPManagerCommand) {
            PVPManagerCommand pvpManagerCommand = (PVPManagerCommand) command;
            PluginCommand pluginCommand = getCommand(command.getName());
            if(pluginCommand != null)
                pluginCommand.setExecutor(pvpManagerCommand.getExecutor());
        }
        return subCommands.register("pvpmanager", command);
    }

    public Permission getPermission() {
        return permission;
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

    private class ListCommand extends PVPManagerCommand implements CommandExecutor {
        private ListCommand() {
            super(null, "list");
            setExecutor(this);
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            StringBuilder sb = new StringBuilder();
            for (com.ttaylorr.uhc.pvp.core.gamemodes.GameMode gameMode : userManager.getGameModes()) {
                Set<Player> players = gameMode.getPlayers();
                sb
                        .append(ChatColor.GOLD)
                        .append(capitalize(gameMode.getName()))
                        .append(ChatColor.GRAY + "◆" + ChatColor.GOLD)
                        .append(players.size())
                        .append("" + ChatColor.GOLD + ": " + ChatColor.RESET);
                if(players.isEmpty()) {
                    sb.append("" + ChatColor.RESET + ChatColor.STRIKETHROUGH).append("    ");
                } else {
                    sb.append(StringUtils.join(Iterables.transform(players, new Function<Player, Object>() {
                        @Override
                        public Object apply(Player player) {
                            return player.getDisplayName();
                        }
                    }).iterator(), ChatColor.RESET + ", "));
                }
                sb.append("\n");
            }

            commandSender.sendMessage(sb.toString());

            return true;
        }

        String capitalize(String name) {
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
    }
}
