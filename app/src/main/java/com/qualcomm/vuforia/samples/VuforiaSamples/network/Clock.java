package com.qualcomm.vuforia.samples.VuforiaSamples.network;

public class Clock {

    private static Clock clock;
    private long time;
    private long dt;

    Clock() {
    }

    public static Clock getInstance() {
        if(clock == null) {
            clock = new Clock();
        }
        return clock;
    }

    public void synchronizeTime() {
        time = System.currentTimeMillis();
    }

    public void synchronizeTime(long dt) {
        this.dt = dt;
        time = System.currentTimeMillis() - dt;
    }

    public long getTime() {
        return System.currentTimeMillis() - time;
    }
}
