package com.ttaylorr.uhc.pvp.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class Kit implements InventoryHolder {

    private ItemStack[] itemStacks;
    private final PlayerInventory inventory;

    public Kit(ConfigurationSection kitConfig) {
        inventory = parseConfig(kitConfig);
        itemStacks = getItemStacks(inventory);
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
        if(clear) inventory.setContents(getInventory().getContents());
        else inventory.addItem(getItemStacks());
    }

    private ItemStack[] getItemStacks() {
        return itemStacks;
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    private static PlayerInventory parseConfig(ConfigurationSection kitConfig) {
        return (PlayerInventory) ConfigurationSerialization.deserializeObject(kitConfig.getValues(true));
    }

    private static ItemStack[] getItemStacks(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        List<ItemStack> nonNullContents = new ArrayList<>(contents.length);
        for(ItemStack content : contents) {
            if(content != null)
                nonNullContents.add(content);
        }
        return nonNullContents.toArray(new ItemStack[nonNullContents.size()]);
    }
}
