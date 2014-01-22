package com.ttaylorr.uhc.pvp.util;

public class Continuation {
    Continuation next;

    boolean called;

    public Continuation() {
        this(null);
    }

    public Continuation(Continuation next) {
        this.next = next;
    }
    public void success() {
        if(next != null) next.success();
    }

    public void failure() {
        if(next != null) next.failure();
    }

    protected Continuation getNext() {
        return next;
    }

    private static Continuation empty = new Continuation();

    public static Continuation empty() {
        return empty;
    }
}
