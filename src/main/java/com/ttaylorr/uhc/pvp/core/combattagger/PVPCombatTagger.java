package com.ttaylorr.uhc.pvp.core.combattagger;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import nl.dykam.dev.reutil.data.ComponentHandle;
import nl.dykam.dev.reutil.data.ComponentManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PVPCombatTagger implements CombatTagger {
    private final PVPManagerPlugin plugin;
    private final ComponentHandle<Player, PlayerTagData> tagHandle;
    ConfigurationSection config;
    CommandMatcher commandMatcher;

    public PVPCombatTagger(PVPManagerPlugin plugin) {
        this.plugin = plugin;
        config = this.plugin.getConfig().getConfigurationSection("combattag");
        commandMatcher = CommandMatcher.construct(config);
        tagHandle = ComponentManager.get(plugin).get(PlayerTagData.class);
    }

    @Override
    public void tag(Player defender, Player attacker) {
        PlayerTagData defenderData = tagHandle.get(defender);
        if (defenderData == null)
            return;
        PlayerTagData attackerData = tagHandle.get(attacker);
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
        PlayerTagData playerTagData = tagHandle.get(player);
        if (playerTagData == null)
            return;
        playerTagData.untag();
    }

    @Override
    public boolean isTagged(Player player) {
        PlayerTagData playerTagData = tagHandle.get(player);
        return playerTagData != null && playerTagData.isTagged();
    }

    public void subscribe(Player player) {
        tagHandle.ensure(player);
    }
    public void unsubscribe(Player player) {
        tagHandle.remove(player);
    }
}
