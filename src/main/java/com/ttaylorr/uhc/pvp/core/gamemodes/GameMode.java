package com.ttaylorr.uhc.pvp.core.gamemodes;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.core.UserManager;
import com.ttaylorr.uhc.pvp.util.Continuation;
import net.milkbowl.vault.permission.Permission;
import nl.dykam.dev.spector.Spector;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class GameMode {
    private Set<Player> players;
    private PVPManagerPlugin plugin;
    private Spector spector;
    private String name;

    protected abstract void onEnter(Player p);

    /**
     * Will cause the player to exit the gamemode.
     * @param p The player
     * @param continuation The followup once the player exited the gamemode
     */
    protected abstract void onExit(Player p, Continuation continuation);

    /**
     * The supplied player is immediately exited from the gamemode,
     * regardless of the state he is in. Usually because of unloading etc.
     * @param p The player
     */
    protected abstract void onImmediateExit(Player p);

    protected GameMode(PVPManagerPlugin plugin, Spector spector, String name) {
        this.plugin = plugin;
        this.spector = spector;
        this.name = name;
        players = new HashSet<>();
    }

    public void enter(Player player) {
        plugin.getDataManager().get(player, UserManager.UserData.class).gameMode = this;
        players.add(player);
        spector.assignTo(player);
        Permission permission = plugin.getPermission();
        if(permission != null)
            permission.playerAddGroup(player, "PVPManager-" + name);
        onEnter(player);
    }

    public void exit(final Player player, final Continuation continuation) {
        onExit(player, new Continuation(continuation) {
            @Override
            public void success() {
                remove(player);
                super.success();
            }
        });
    }

    public void immediateExit(Player p) {
        remove(p);
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

    public Iterable<Player> getPlayers() {
        return players;
    }

    public PVPManagerPlugin getPlugin() {
        return plugin;
    }
}
