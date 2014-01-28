package com.ttaylorr.uhc.pvp.core.combattagger;

import com.ttaylorr.uhc.pvp.events.PlayerTaggedEvent;
import com.ttaylorr.uhc.pvp.events.PlayerUntaggedEvent;
import com.ttaylorr.uhc.pvp.util.Message;
import nl.dykam.dev.reutil.data.Component;
import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.data.annotations.Instantiation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Defaults(instantiation = Instantiation.Manual, global = false)
class PlayerTagData extends Component<Player> implements Iterable<Player> {
    Map<Player, Long> tags;
    long tagTime = -1;
    BukkitTask tagOffAnnouncer;

    public PlayerTagData() {
        tags = new HashMap<>();
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
        return tags.containsKey(tagger) && tags.get(tagger) + getContext().getPlugin().getConfig().getInt("combattag.time") >= getTimeStamp();
    }

    public boolean isTagged() {
        return tagTime + getContext().getPlugin().getConfig().getInt("combattag.time") >= getTimeStamp();
    }

    public void tag(Player tagger) {
        tagTime = getTimeStamp();
        tags.put(tagger, tagTime);
        if (!getContext().getPlugin().getConfig().getString("combattag.display.mode", "single").equals("none")) {
            if (tagOffAnnouncer != null)
                tagOffAnnouncer.cancel();
            tagOffAnnouncer = Bukkit.getScheduler().runTaskLater(getContext().getPlugin(), new Runnable() {
                @Override
                public void run() {
                    untag();
                }
            }, getContext().getPlugin().getConfig().getInt("combattag.time", 100));
        }
    }

    public void untag() {
        if(!isTagged())
            return;
        PlayerUntaggedEvent event = new PlayerUntaggedEvent(getObject());
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled())
            return;

        tagTime = -1;
        tags.clear();
        if (tagOffAnnouncer != null) {
            tagOffAnnouncer.cancel();
            tagOffAnnouncer = null;
            Message.success(getObject(), getContext().getPlugin().getConfig().getString("combattag.display.untag"));
        }
    }

    public void tryTag(Player tagger) {
        PlayerTaggedEvent event = new PlayerTaggedEvent(getObject());
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled())
            return;
        switch (getContext().getPlugin().getConfig().getString("combattag.display.mode", "single")) {
            case "single":
                if (!isTagged()) {
                    Message.warn(tagger, getContext().getPlugin().getConfig().getString("combattag.display.tag"));
                }
                break;
            case "per-person":
                if (!isTaggedBy(getObject())) {
                    Message.warn(tagger, getContext().getPlugin().getConfig().getString("combattag.display.tag"));
                }
                break;
            case "retag":
                Message.warn(tagger, getContext().getPlugin().getConfig().getString("combattag.display.tag"));
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