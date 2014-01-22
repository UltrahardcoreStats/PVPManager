package com.ttaylorr.uhc.pvp.core.combattagger;

import com.ttaylorr.uhc.pvp.events.PlayerTaggedEvent;
import com.ttaylorr.uhc.pvp.events.PlayerUntaggedEvent;
import com.ttaylorr.uhc.pvp.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class PlayerTagData implements Iterable<Player> {
    Player player;
    Map<Player, Long> tags;
    long tagTime = -1;
    Plugin plugin;
    BukkitTask tagOffAnnouncer;

    public PlayerTagData(Plugin plugin, Player player) {
        this.player = player;
        tags = new HashMap<>();
        this.plugin = plugin;
    }

    public Player getPlayer() {
        return player;
    }

    public long getTagTimeBy(Player tagger) {
        if (isTaggedBy(tagger))
            return tags.get(tagger);
        return -1;
    }

    public long getTagTime() {
        if (isTagged())
            return tagTime;
        return -1;
    }

    public boolean isTaggedBy(Player tagger) {
        return tags.containsKey(tagger) && tags.get(tagger) + plugin.getConfig().getInt("combattag.time") >= getTimeStamp();
    }

    public boolean isTagged() {
        return tagTime + plugin.getConfig().getInt("combattag.time") >= getTimeStamp();
    }

    public void tag(Player tagger) {
        tagTime = getTimeStamp();
        tags.put(tagger, tagTime);
        if (!plugin.getConfig().getString("combattag.display.mode", "single").equals("none")) {
            if (tagOffAnnouncer != null)
                tagOffAnnouncer.cancel();
            tagOffAnnouncer = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    untag();
                }
            }, plugin.getConfig().getInt("combattag.time", 100));
        }
    }

    public void untag() {
        if(!isTagged())
            return;
        PlayerUntaggedEvent event = new PlayerUntaggedEvent(player);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled())
            return;

        tagTime = -1;
        tags.clear();
        if (tagOffAnnouncer != null) {
            tagOffAnnouncer.cancel();
            Message.success(player, plugin.getConfig().getString("combattag.display.untag"));
        }
    }

    public void tryTag(Player tagger) {
        PlayerTaggedEvent event = new PlayerTaggedEvent(player);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled())
            return;
        switch (plugin.getConfig().getString("combattag.display.mode", "single")) {
            case "single":
                if (!isTagged()) {
                    Message.warn(tagger, plugin.getConfig().getString("combattag.display.tag"));
                }
                break;
            case "per-person":
                if (!isTaggedBy(player)) {
                    Message.warn(tagger, plugin.getConfig().getString("combattag.display.tag"));
                }
                break;
            case "retag":
                Message.warn(tagger, plugin.getConfig().getString("combattag.display.tag"));
                break;
        }
        tag(tagger); // Always tag, but only show the message if it is a new tag.
    }

    /**
     * Returns the timestamp in ticks
     * @return The timestamp in ticks
     */
    public long getTimeStamp() {
        return System.currentTimeMillis() / 1000L * 20L;
    }

    @Override
    public Iterator<Player> iterator() {
        return new Iterator<Player>() {
            Iterator<Player> allPlayers;
            Player nextPlayer;

            {
                {
                    allPlayers = tags.keySet().iterator();
                    next();
                }
            }

            @Override
            public boolean hasNext() {
                return nextPlayer != null;
            }

            @Override
            public Player next() {
                Player returnedPlayer = nextPlayer;
                while (allPlayers.hasNext()) {
                    Player player = allPlayers.next();
                    if (isTaggedBy(player)) {
                        nextPlayer = player;
                        break;
                    }
                }
                return returnedPlayer;
            }

            @Override
            public void remove() {
                // derp, can't remove, we are already further ahead
            }
        };
    }
}