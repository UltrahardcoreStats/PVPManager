package com.ttaylorr.uhc.pvp.util;

public class Continuation {
    Continuation next;
    public Continuation() {
        this(null);
    }

    protected Continuation(Continuation next) {
        this.next = next;
    }

    public void success() {
        if(next != null) next.success();
    }

    public void failure() {
        if(next != null) next.failure();
    }

    private static Continuation empty = new Continuation();

    public static Continuation empty() {
        return empty;
    }
}
