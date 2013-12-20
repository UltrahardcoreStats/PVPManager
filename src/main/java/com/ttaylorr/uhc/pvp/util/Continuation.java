package com.ttaylorr.uhc.pvp.util;

public class Continuation {
    Continuation next;

    boolean called;

    public Continuation() {
        this(null);
    }

    protected Continuation(Continuation next) {
        this.next = next;
    }
    public void success() {
        validate();
        if(next != null) next.success();
    }

    public void failure() {
        validate();
        if(next != null) next.failure();
    }

    public void validate() {
        if(called)
            throw new IllegalStateException("Continuation has already been called");
        called = true; // Not foolproof, might not be called by subclass
    }

    protected Continuation getNext() {
        return next;
    }

    private static Continuation empty = new Continuation();

    public static Continuation empty() {
        return empty;
    }
}
