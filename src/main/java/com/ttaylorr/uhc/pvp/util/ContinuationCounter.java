package com.ttaylorr.uhc.pvp.util;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

public class ContinuationCounter extends Continuation implements Runnable {
    private int seconds;
    private final Runnable tick;
    private final Runnable success;
    private BukkitTask task;

    public ContinuationCounter(Continuation continuation, int seconds, Runnable tick, Runnable success) {
        super(continuation);
        this.seconds = seconds;
        this.tick = tick;
        this.success = success;
        if(tick != null) {
            tick.run();
        }
    }

    @Override
    public void success() {
        validate();
        task = Bukkit.getScheduler().runTaskTimer(PVPManagerPlugin.get(), this, 20, 20);
    }

    @Override
    public void run() {
        if(--seconds == 0) {
            task.cancel();
            getNext().success();
            if(success != null) {
                success.run();
            }
        } else if (tick != null) {
            tick.run();
        }
    }
}
