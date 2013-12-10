package com.ttaylorr.uhc.pvp.services.core;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.ttaylorr.uhc.pvp.services.PVPUtility;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class UHCMagicWall implements PVPUtility {
    ProtectedRegion region;
    List<ProtectedRegion> children;

    public UHCMagicWall(String world, String region) {
        this.region = WorldGuardPlugin.inst().getRegionManager(Bukkit.getWorld(world)).getRegion(region);
        children = getChildren(this.region, Bukkit.getWorld(world));
    }

    @Override
    public void subscribe(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unsubscribe(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Fetches the subregions of a WorldGuard @ProtectedRegion.
     *
     * @param region The region to get the children from.
     * @return The children.
     */
    private List<ProtectedRegion> getChildren(ProtectedRegion region, World world) {
        HashMap<String, List<ProtectedRegion>> subRegions = new HashMap<String, List<ProtectedRegion>>();
        for (ProtectedRegion subRegion : WorldGuardPlugin.inst().getRegionManager(world).getRegions().values()) {
            String parentID = subRegion.getParent().getId();
            if (!subRegions.containsKey(parentID)) {
                subRegions.put(parentID, new ArrayList<ProtectedRegion>());
            }
            subRegions.get(parentID).add(subRegion);
        }
        Queue<ProtectedRegion> regionQueue = new LinkedList<ProtectedRegion>();
        regionQueue.add(region);
        List<ProtectedRegion> result = new LinkedList<ProtectedRegion>();
        while (regionQueue.size() > 0) {
            ProtectedRegion current = regionQueue.remove();
            result.add(current);
            if (!subRegions.containsKey(current.getId()))
                continue;
            for (ProtectedRegion subRegion : subRegions.get(current.getId())) {
                regionQueue.add(subRegion);
            }
        }
        return result;
    }
}
