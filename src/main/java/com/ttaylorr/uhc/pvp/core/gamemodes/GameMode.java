package com.ttaylorr.uhc.pvp.core.gamemodes;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.util.Continuation;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class GameMode {
    private Set<Player> players;
    private PVPManagerPlugin plugin;

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

    protected GameMode(PVPManagerPlugin plugin) {
        this.plugin = plugin;
        players = new HashSet<>();
    }

    public void enter(Player player) {
        onEnter(player);
        players.add(player);
    }

    public void exit(final Player player, final Continuation continuation) {
        onExit(player, new Continuation(continuation) {
            @Override
            public void success() {
                players.remove(player);
                super.success();
            }
        });
    }

    public void immediateExit(Player p) {
        onImmediateExit(p);
        players.remove(p);
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