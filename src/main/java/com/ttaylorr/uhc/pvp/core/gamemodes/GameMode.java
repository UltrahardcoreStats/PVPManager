package com.ttaylorr.uhc.pvp.core.gamemodes;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.core.UserManager;
import com.ttaylorr.uhc.pvp.util.Continuation;
import net.milkbowl.vault.permission.Permission;
import nl.dykam.dev.reutil.data.ComponentHandle;
import nl.dykam.dev.reutil.data.ComponentManager;
import nl.dykam.dev.spector.Spector;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class GameMode {
    private final ComponentHandle<Player, UserManager.UserData> dataHandle;
    private Set<Player> players;
    private PVPManagerPlugin plugin;
    private Spector spector;
    private String name;
    private String permissionGroup;

    protected abstract void onEnter(Player p);

    /**
     * Will cause the player to exit the gamemode.
     * @param p The player
     * @param continuation The followup once the player exited the gamemode
     */
    protected abstract Continuation onExit(Player p, Continuation continuation);

    /**
     * The supplied player is immediately exited from the gamemode,
     * regardless of the state he is in. Usually because of unloading etc.
     * @param p The player
     */
    protected abstract void onImmediateExit(Player p);

    public String getPermissionGroup() {
        return permissionGroup;
    }

    @SuppressWarnings("unchecked")
    protected GameMode(PVPManagerPlugin plugin, Spector spector, String name) {
        this.plugin = plugin;
        this.spector = spector;
        this.name = name;

        dataHandle = ComponentManager.get(plugin).get(UserManager.UserData.class);

        players = new HashSet<>();
    }

    public void enter(Player player) {
        dataHandle.get(player).gameMode = this;
        players.add(player);
        Permission permission = plugin.getPermission();
        if(permission != null) {
            permissionGroup = "PVPManager-" + name;
            permission.playerAddGroup(player, permissionGroup);
        }
        onEnter(player);
        spector.assignTo(player);
    }

    public Continuation exit(final Player player, final Continuation continuation) {
        return onExit(player, new Continuation(continuation) {
            @Override
            public void success() {
                remove(player);
                super.success();
            }
        });
    }

    public void immediateExit(Player p) {
        remove(p);
        onImmediateExit(p);
    }

    private void remove(Player p) {
        players.remove(p);
        Permission permission = plugin.getPermission();
        if(permission != null)
            permission.playerRemoveGroup(p, "PVPManager-" + name);
    }

    public boolean isInGameMode(Player p) {
        return players.contains(p);
    }

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public ComponentHandle<Player, UserManager.UserData> getDataHandle() {
        return dataHandle;
    }

    public PVPManagerPlugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public Spector getSpector() {
        return spector;
    }
}
