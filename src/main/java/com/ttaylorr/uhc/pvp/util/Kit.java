package com.ttaylorr.uhc.pvp.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class Kit extends PersistentBase {

    public static final ItemStack[] EMPTY_ITEM_STACKS = new ItemStack[0];
    private ItemStack[] compressed;
    private ItemStack[] contents, armor;

    public Kit(ConfigurationSection kitConfig, String path) {
        setConfig(kitConfig, path);
        load();
    }

    /**
     * Adds this kit to the inventory, or replaces it.
     * @param human The holder of the inventory to apply it to
     * @param clear Whether to clear the given inventory
     */
    public void apply(HumanEntity human, boolean clear) {
        apply(human.getInventory(), clear);
    }

    /**
     * Adds this kit to the inventory, or replaces it.
     * @param inventory The inventory to apply it to
     * @param clear     Whether to clear the given inventory
     */
    public void apply(PlayerInventory inventory, boolean clear) {
        if(clear) {
            inventory.setContents(contents);
            inventory.setArmorContents(armor);
        }
        else inventory.addItem(getCompressed());
    }

    private ItemStack[] getCompressed() {
        return compressed;
    }

    public void read(HumanEntity human) {
        read(human.getInventory());
    }

    public void read(PlayerInventory inventory) {
        contents = inventory.getContents();
        armor = inventory.getArmorContents();
        compress();
    }

    private void compress() {
        compressed = compress(contents, armor);
    }

    private static ItemStack[] compress(ItemStack[]... contents) {
        List<ItemStack> nonNullContents = new ArrayList<>();
        for(ItemStack[] subContents : contents) {
            for(ItemStack content : subContents) {
                if(content != null)
                    nonNullContents.add(content);
            }
        }
        return nonNullContents.toArray(new ItemStack[nonNullContents.size()]);
    }

    @Override
    public void load(boolean clear) {
        ConfigurationSection config = getConfig().getConfigurationSection(getConfigPath());
        if(config != null) {
            contents = getItemStacksFromConfig(config, "contents");
            armor = getItemStacksFromConfig(config, "armor");
            compress();
        }
    }

    @SuppressWarnings("unchecked")
    private ItemStack[] getItemStacksFromConfig(ConfigurationSection config, String contents1) {
        List<ItemStack> list = (List<ItemStack>)config.get(contents1);
        return list.toArray(EMPTY_ITEM_STACKS);
    }

    @Override
    public void save(boolean clear) {
        getConfig().set(getConfigPath() + ".contents", contents);
        getConfig().set(getConfigPath() + ".armor", armor);
    }
}
