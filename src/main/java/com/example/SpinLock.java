package com.example;

public class SpinLock {
    private boolean isLocked = false;

    public synchronized void lock() throws InterruptedException {
        this.isLocked = true;
        while (this.isLocked) {
            wait();
        }
    }

    public synchronized void unlock() {
        this.isLocked = false;
        notify();
    }
}
