package com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by Hans-Olav on 02.03.2015.
 */
public class TrackData {


    private static TrackData sTrackData = null;
    private ArrayList<Float> xPath;
    private ArrayList<Float> yPath;
    private ArrayList<String> partPath;
    private ArrayList<Integer> rotationPath;

    protected TrackData() {
        // Exists only to defeat instantiation.
    }

    public static synchronized TrackData getInstance(){
        if(sTrackData==null){
            sTrackData = new TrackData();
        }
        return sTrackData;
    }

    public ArrayList<Float> getxPath() {
        return xPath;
    }

    public void setxPath(ArrayList<Float> xPath) {
        this.xPath = xPath;
    }

    public ArrayList<Float> getyPath() {
        return yPath;
    }

    public void setyPath(ArrayList<Float> yPath) {
        this.yPath = yPath;
    }

    public ArrayList<String> getPartPath() {
        return partPath;
    }

    public void setPartPath(ArrayList<String> partPath) {
        this.partPath = partPath;
    }

    public void setRotationPath(ArrayList<Integer> rotationPath) {
        this.rotationPath = rotationPath;
    }

    public ArrayList<Integer> getRotationPath() {
        return rotationPath;
    }
}
