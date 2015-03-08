package com.qualcomm.vuforia.samples.VuforiaSamples.network;

import java.io.Serializable;
import java.util.ArrayList;

public class TrackPacket implements Serializable {

    private ArrayList<Float> xPath;
    private ArrayList<Float> yPath;
    private ArrayList<String> partPath;
    private ArrayList<Integer> rotationPath;


    public TrackPacket() {
        xPath = new ArrayList<Float>();
        yPath = new ArrayList<Float>();
        partPath = new ArrayList<String>();
        rotationPath = new ArrayList<Integer>();
    }

    public ArrayList<Float> getXPath() {
        return xPath;
    }

    public ArrayList<Float> getYPath() {
        return yPath;
    }

    public ArrayList<String> getPartPath() {
        return partPath;
    }

    public ArrayList<Integer> getRotationPath() {
        return rotationPath;
    }

    public void setXPath(ArrayList<Float> xPath) {
        this.xPath = new ArrayList<Float>(xPath);
    }

    public void setYPath(ArrayList<Float> yPath) {
        this.yPath = new ArrayList<Float>(yPath);
    }

    public void setPartPath(ArrayList<String> partPath) {
        this.partPath = new ArrayList<String>(partPath);
    }

    public void setRotationPath(ArrayList<Integer> rotationPath) {
        this.rotationPath = new ArrayList<Integer>(rotationPath);
    }

}
