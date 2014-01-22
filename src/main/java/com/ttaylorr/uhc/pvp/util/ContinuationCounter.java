package com.ttaylorr.uhc.pvp.util;

import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

public class ContinuationCounter extends Continuation implements Runnable {
    private final CommandSender receiver;
    private int seconds;
    private final String tickMessage;
    private final String successMessage;
    private BukkitTask task;

    public ContinuationCounter(Continuation continuation, CommandSender receiver, int seconds, String tickMessage, String successMessage) {
        super(continuation);
        this.receiver = receiver;
        this.seconds = seconds;
        this.tickMessage = tickMessage;
        this.successMessage = successMessage;
        if(tickMessage != null) {
            Message.message(receiver, String.format(tickMessage, seconds));
        }
    }

    @Override
    public void success() {
        task = Bukkit.getScheduler().runTaskTimer(PVPManagerPlugin.get(), this, 20, 20);
    }

    @Override
    public void failure() {
        task.cancel();
        getNext().failure();
    }

    @Override
    public void run() {
        if(--seconds == 0) {
            task.cancel();
            getNext().success();
            if(successMessage != null) {
                Message.message(receiver, successMessage);
            }
        } else if (tickMessage != null) {
            Message.message(receiver, String.format(tickMessage, seconds));
        }
    }
}
