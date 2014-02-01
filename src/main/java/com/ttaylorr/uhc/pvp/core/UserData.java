package com.ttaylorr.uhc.pvp.core;

import com.ttaylorr.uhc.pvp.core.gamemodes.GameMode;
import com.ttaylorr.uhc.pvp.util.Continuation;
import nl.dykam.dev.reutil.data.Component;
import org.bukkit.entity.Player;

/**
* Created by Dykam on 31-1-14.
*/
public class UserData extends Component<Player> {
    public GameMode gameMode;

    public boolean transitioning;
    public Continuation transition;
    public boolean isSubscribed() {
        return gameMode != null;
    }
}
