package com.ttaylorr.uhc.pvp.core.combattagger;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PVPCombatTagger implements CombatTagger {
    private final PVPManagerPlugin plugin;
    ConfigurationSection config;
    CommandMatcher commandMatcher;
    HashMap<Player, PlayerTagData> tagData;

    public PVPCombatTagger(PVPManagerPlugin plugin) {
        this.plugin = plugin;
        config = this.plugin.getConfig().getConfigurationSection("combattag");
        commandMatcher = CommandMatcher.construct(config);
        tagData = new HashMap<>();
    }

    @Override
    public void tag(Player defender, Player attacker) {
        PlayerTagData defenderData = tagData.get(defender);
        if (defenderData == null)
            return;
        PlayerTagData attackerData = tagData.get(attacker);
        if (attackerData == null)
            return;

        defenderData.tryTag(attacker);

        if(config.getBoolean("tagback", true)) {
            attackerData.tryTag(defender);
        }
    }

    private void tryTag(Player defender, Player attacker) {
    }

    @Override
    public void untag(Player player) {
        PlayerTagData playerTagData = tagData.get(player);
        if (playerTagData == null)
            return;
        playerTagData.untag();
    }

    @Override
    public boolean isTagged(Player player) {
        PlayerTagData playerTagData = tagData.get(player);
        return playerTagData != null && playerTagData.isTagged();
    }

    public void subscribe(Player player) {
        if(tagData.containsKey(player))
            return;
        tagData.put(player, new PlayerTagData(plugin, player));
    }
    public void unsubscribe(Player player) {
        tagData.remove(player);
    }
}
