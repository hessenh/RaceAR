package com.qualcomm.vuforia.samples.VuforiaSamples.network;

public interface PacketHandler {
    public void carPacketHandler(CarPacket packet);
    public void trackPacketHandler(TrackPacket packet);
    public void newConnectionHandler(Connection connection);
}
