package com.ttaylorr.uhc.pvp.core.combattagger;

import org.bukkit.ChatColor;
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
    tags = new HashMap<Player, Long>();
    this.plugin = plugin;
  }

  public Player getPlayer() {
    return player;
  }

  public long getTagTimeBy(Player tagger) {
    if(isTaggedBy(tagger))
      return tags.get(tagger);
    return -1;
  }

  public long getTagTime() {
    if(isTagged())
      return tagTime;
    return -1;
  }

  public boolean isTaggedBy(Player tagger) {
      return false;
//    return tags.containsKey(tagger) && tags.get(tagger) + plugin.getConfig().getInt("duration") * 50 >= PvP.getTime();
  }

  public boolean isTagged() {
      return false;
//    return tagTime + plugin.getConfig().getInt("duration") * 50 >= PvP.getTime();
  }

  public void tag(Player tagger) {
//    tagTime = PvP.getTime();
//    tags.put(tagger, tagTime);
//    if(plugin.getConfig().getBoolean("notify-untag", false)) {
//      if(tagOffAnnouncer != null)
//        tagOffAnnouncer.cancel();
//      tagOffAnnouncer = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
//        @Override
//        public void run() {
//          untag();
//        }
//      }, plugin.getConfig().getInt("duration", 20 * 20));
//    }
  }

  public void untag() {
    tagTime = -1;
    tags.clear();
    if(tagOffAnnouncer != null) {
      tagOffAnnouncer.cancel();
      player.sendMessage(ChatColor.GREEN + "[Broncin] " + ChatColor.GOLD + "You are no longer tagged");
    }
  }

  public void tryTag(Player tagger) {
//    if (!isTaggedBy(tagger)) {
//      if(tagger.equals(player)) {
//        if(!plugin.getConfig().getBoolean("tag-self", false))
//          return;
//        tagger.sendMessage(ChatColor.GREEN + "[Broncin] " + ChatColor.GOLD + "Tagged yourself");
//      } else if(plugin.getConfig().getBoolean("tag-back", false)) {
//        tagger.sendMessage(ChatColor.GREEN + "[Broncin] " + ChatColor.GOLD + "Tagged and tagback: " + ChatColor.WHITE + TagNames.getName(plugin, tagger, player, TagNames.Side.Defender));
//      } else {
//        tagger.sendMessage(ChatColor.GREEN + "[Broncin] " + ChatColor.GOLD + "Tagged: " + ChatColor.WHITE + TagNames.getName(plugin, tagger, player, TagNames.Side.Defender));
//      }
//      player.sendMessage(ChatColor.GREEN + "[Broncin] " + ChatColor.GOLD + "Tagged By: " + ChatColor.WHITE + TagNames.getName(plugin, player, tagger, TagNames.Side.Attacker));
//    }
//    tag(tagger); // Always tag, but only show the message if it is a new tag.
  }

  @Override
  public Iterator<Player> iterator() {
    return new Iterator<Player>() {
      Iterator<Player> allPlayers;
      Player nextPlayer;
      {{
        allPlayers = tags.keySet().iterator();
        next();
      }}
      @Override
      public boolean hasNext() {
        return nextPlayer != null;
      }

      @Override
      public Player next() {
        Player returnedPlayer = nextPlayer;
        while (allPlayers.hasNext()) {
          Player player = allPlayers.next();
          if(isTaggedBy(player)) {
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