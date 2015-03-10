package com.qualcomm.vuforia.samples.VuforiaSamples.network;

import java.io.Serializable;

public class ClientPacket implements Serializable {

    public void setTime(int time) {
        this.time = time;
    }

    public enum ClientAction implements Serializable {
        TIME, START, WIN, END,READY;
    }

    private ClientAction action;
    private long time;

    public ClientPacket(ClientAction action) {
        this.action = action;
        time = -1;
    }

    public ClientPacket(ClientAction action, long time) {
        this.action = action;
        this.time = time;
    }

    public ClientAction getAction() {
        return action;
    }

    public long getTime() {
        return time;
    }
}
