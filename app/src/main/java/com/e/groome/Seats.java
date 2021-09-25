package com.e.groome;

public class Seats {
    private String seat;
    private boolean free, notFree;

    public Seats() {
    }

    public Seats(String seat, boolean free, boolean notFree) {
        this.seat = seat;
        this.free = free;
        this.notFree = notFree;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public boolean isNotFree() {
        return notFree;
    }

    public void setNotFree(boolean notFree) {
        this.notFree = notFree;
    }
}
