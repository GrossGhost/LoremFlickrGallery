package com.test.loremflickr.model;

public class LoremFlickrImage {
    private String file;
    private String owner;
    private int lock;

    public String getImage() {
        return file;
    }

    public String getOwner() {
        return owner;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }
}
