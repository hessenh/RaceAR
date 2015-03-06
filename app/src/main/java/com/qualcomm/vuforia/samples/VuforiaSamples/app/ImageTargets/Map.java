package com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets;

import android.content.Context;

import com.qualcomm.vuforia.samples.SampleApplication.utils.ObjObject;

import java.util.ArrayList;

/**
 * Created by Hans-Olav on 27.02.2015.
 */
public class Map {


    private final float scaleFactor;
    private final float objectSize;
    private ArrayList<Float> xPath;
    private ArrayList<Float> yPath;
    private ArrayList<Float> zPath;
    private float circuitPosX;
    private float circuitPosY;
    private int smallStep = 0;
    private int step;
    private final int NUMBER_OF_STEPS = 2;
    private float circuitPosZ;
    private ArrayList<String> pathName;
    private ArrayList<Integer> pathRotation;


    public Map(float scaleFactor,float objectSize){
        System.out.println("Map created");
        this.scaleFactor = scaleFactor;
        this.objectSize = objectSize;
        //Final pathName lists
        xPath = new ArrayList<Float>();
        yPath = new ArrayList<Float>();
    }


    public boolean isCircuit(ArrayList<ObjObject> objectList) {

        boolean test = false;
        boolean result = false;


        for(int i =1;i<objectList.size();i++){
            xPath = new ArrayList<Float>();
            yPath = new ArrayList<Float>();
            zPath = new ArrayList<Float>();
            pathName = new ArrayList<String>();
            pathRotation = new ArrayList<Integer>();

            test = walkPath(objectList.get(i),objectList.get(i).getID(),10);
            if(test){
                //To get out of the loop
                i=objectList.size();
                result = true;

            }
        }
        return result;
    }

    private boolean walkPath(ObjObject object,int i,int from) {
        xPath.add(object.getX());
        yPath.add(object.getY());
        pathName.add(object.getPartName());
        pathRotation.add(object.getRotation());
        if(object.getNorthObject()!=null && from !=0){
            if(object.getNorthObject().getID()==i){
                xPath.add(object.getNorthObject().getX());
                yPath.add(object.getNorthObject().getY());
                zPath.add(object.getNorthObject().getZ());
                pathName.add(object.getNorthObject().getPartName());
                pathRotation.add(object.getNorthObject().getRotation());

                //System.out.println("true north");
                return true;
            }
            else{
                //System.out.println("Trying " + object.getNorthObject().getID() + "north");
                return walkPath(object.getNorthObject(),i,1);
            }
        }
        else if(object.getSouthObject()!=null && from !=1){
            if(object.getSouthObject().getID()==i){
                xPath.add(object.getSouthObject().getX());
                yPath.add(object.getSouthObject().getY());
                zPath.add(object.getSouthObject().getZ());
                pathName.add(object.getSouthObject().getPartName());
                pathRotation.add(object.getSouthObject().getRotation());
                //System.out.println("true south");
                return true;
            }
            else{
                //System.out.println("Trying " + object.getSouthObject().getID() + " south");
                return walkPath(object.getSouthObject(),i,0);
            }
        }
        else if(object.getWestObject()!=null && from !=2){
            if(object.getWestObject().getID()==i){
                xPath.add(object.getWestObject().getX());
                yPath.add(object.getWestObject().getY());
                zPath.add(object.getWestObject().getZ());
                pathName.add(object.getWestObject().getPartName());
                pathRotation.add(object.getWestObject().getRotation());
                //System.out.println("true west");
                return true;
            }
            else{
                //System.out.println("Trying " + object.getWestObject().getID() + "west");
                return walkPath(object.getWestObject(),i,3);
            }
        }
        else if(object.getEastObject()!=null && from !=3){
            if(object.getEastObject().getID()==i){
                xPath.add(object.getEastObject().getX());
                yPath.add(object.getEastObject().getY());
                zPath.add(object.getEastObject().getZ());
                pathName.add(object.getEastObject().getPartName());
                pathRotation.add(object.getEastObject().getRotation());
                //System.out.println("true east");
                return true;
            }
            else{
                //System.out.println("Trying " + object.getEastObject().getID() + " east");
                return walkPath(object.getEastObject(),i,2);
            }
        }
        else {
            return false;
        }
    }

    public float getCircuitPosX() {

        smallStep++;
        if(smallStep >NUMBER_OF_STEPS){
            smallStep = 0;
            step++;
            if(step>=xPath.size()-1){
                step = 0;
            }
        }

        return xPath.get(step) + (getXDirection(step)/NUMBER_OF_STEPS)*smallStep;
    }

    private Float getXDirection(int step) {
        return xPath.get(step+1) - xPath.get(step);
    }

    public float getCircuitPosY() {
        return yPath.get(step) + (getYDirection(step)/NUMBER_OF_STEPS)*smallStep;
    }

    private float getYDirection(int step) {
        return yPath.get(step+1) - yPath.get(step);
    }

    public void setCircuitPosY(float circuitPosY) {
        this.circuitPosY = circuitPosY;
    }

    public float getCircuitPosZ() {
        return circuitPosZ;
    }

    public void setCircuitPosZ(float circuitPosZ) {
        this.circuitPosZ = zPath.get(step);
    }

    public ArrayList<String> getPartPath() {
        return pathName;
    }

    public ArrayList<Float> getxPath() {
        return xPath;
    }

    public ArrayList<Float> getyPath() {
        return yPath;
    }

    public ArrayList<Integer> getRotationPath() {
        return pathRotation;
    }
}
