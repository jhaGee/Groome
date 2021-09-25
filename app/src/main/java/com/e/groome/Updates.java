package com.e.groome;

public class Updates {
    private String seat, key;
    private boolean free, notFree;

    public Updates() {
    }

    public Updates(String seat, String key, boolean free, boolean notFree) {
        this.seat = seat;
        this.key = key;
        this.free = free;
        this.notFree = notFree;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
