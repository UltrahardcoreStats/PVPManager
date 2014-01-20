package com.ttaylorr.uhc.pvp.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {
    public static final ItemStack[] EMPTY_ARMOR = new ItemStack[4];
    public static void clear(PlayerInventory inventory) {
        inventory.clear();
        inventory.setArmorContents(EMPTY_ARMOR);
    }
}
