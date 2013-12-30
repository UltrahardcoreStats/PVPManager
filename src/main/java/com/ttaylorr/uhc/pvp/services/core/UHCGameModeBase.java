package com.ttaylorr.uhc.pvp.services.core;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.services.interfaces.GameMode;
import com.ttaylorr.uhc.pvp.util.Continuation;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class UHCGameModeBase extends UHCServiceBase implements GameMode {
    private Set<Player> players;
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

    protected UHCGameModeBase(PVPManagerPlugin plugin) {
        super(plugin);
        players = new HashSet<>();
    }

    @Override
    public void enter(Player player) {
        onEnter(player);
        players.add(player);
    }

    @Override
    public void exit(final Player player, final Continuation continuation) {
        onExit(player, new Continuation(continuation) {
            @Override
            public void success() {
                players.remove(player);
                super.success();
            }
        });
    }

    @Override
    public void immediateExit(Player p) {
        onImmediateExit(p);
        players.remove(p);
    }
}
