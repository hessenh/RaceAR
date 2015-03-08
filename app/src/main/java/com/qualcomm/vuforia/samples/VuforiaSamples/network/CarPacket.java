package com.qualcomm.vuforia.samples.VuforiaSamples.network;

import java.io.Serializable;

public class CarPacket implements Serializable {
    private float x, y;
    private int angle;


    public CarPacket(float x, float y, int angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getAngle() {
        return angle;
    }


}
