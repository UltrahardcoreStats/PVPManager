package com.ttaylorr.uhc.pvp.core;

import nl.dykam.dev.reutil.data.Component;
import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.data.annotations.Persistent;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Persistent
public class UserSettings extends Component<Player> implements ConfigurationSerializable {
    private boolean instantJoinPvp = false;
    private boolean playLobbyMusic = true;
    public UserSettings() {
    }

    public UserSettings(Map<String, Object> data) {
        Object instantJoinPvpObject = data.get("instant join pvp");
        instantJoinPvp = instantJoinPvpObject instanceof Boolean && (boolean)instantJoinPvpObject;
        Object playLobbyMusicObject = data.get("play lobby music");
        playLobbyMusic = playLobbyMusicObject instanceof Boolean && (boolean)playLobbyMusicObject;
    }

    public boolean instantJoinPvp() {
        return instantJoinPvp;
    }

    public void setInstantJoinPvp(boolean instantJoinPvp) {
        this.instantJoinPvp = instantJoinPvp;
    }

    public boolean playLobbyMusic() {
        return playLobbyMusic;
    }

    public void setPlayLobbyMusic(boolean playLobbyMusic) {
        this.playLobbyMusic = playLobbyMusic;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("instant join pvp", instantJoinPvp);
        data.put("play lobby music", playLobbyMusic);
        return data;
    }
}
