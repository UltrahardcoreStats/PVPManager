package com.ttaylorr.uhc.pvp.core.interfaces;

import com.ttaylorr.uhc.pvp.core.SpawnManager;
import com.ttaylorr.uhc.pvp.core.gamemodes.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class SpawnChooser {
    private static final RandomChooser randomChooser = new RandomChooser();
    private static final FarChooser farChooser = new FarChooser();

    public abstract Location choose(Player player, SpawnManager spawns, Context context);
    public static SpawnChooser random() {
        return randomChooser;
    }
    public static SpawnChooser far() {
        return farChooser;
    }
    static class RandomChooser extends SpawnChooser {
        @Override
        public Location choose(Player player, SpawnManager spawns, Context context) {
            return spawns.get((int)(Math.random() * spawns.size()));
        }
    }

    static class FarChooser extends SpawnChooser {
        @Override
        public Location choose(Player player, SpawnManager spawns, Context context) {
            Location furthestLocation = null;
            double furthestDistance = 0;
            for(Location loc : spawns) {
                double closestDistance = Float.MAX_VALUE;
                for(Player otherPlayer : context.gameMode.getPlayers()) {
                    if(otherPlayer == player || !otherPlayer.getWorld().getUID().equals(otherPlayer.getWorld().getUID()))
                        continue;
                    double distance = loc.distance(otherPlayer.getLocation());
                    if(distance < closestDistance)
                        closestDistance = distance;
                }
                if(closestDistance > furthestDistance) {
                    furthestDistance = closestDistance;
                    furthestLocation = loc;
                }
            }
            return furthestLocation != null ? furthestLocation : random().choose(player, spawns, context);
        }
    }

    public static class Context {
        private GameMode gameMode;

        public Context(GameMode gameMode) {
            this.gameMode = gameMode;
        }

        public GameMode getGameMode() {
            return gameMode;
        }
    }
}
