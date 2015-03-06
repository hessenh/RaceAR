package com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets;

/**
 * Created by Hans-Olav on 16.02.2015.
 */
public class BoundingBox {
    private final int id;
    private float [] minValues;
    private float[] maxValues;
    private float x;
    private float y;
    private float z;
    private float sizeX;
    private float sizeY;
    private float sizeZ;


    public BoundingBox(float x,float y,float z,float sizeX,float sizeY,float sizeZ,int id){
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }
    public boolean collide(BoundingBox other){
        //check the X axis
        if(Math.abs(getX() - other.getX()) < getSizeX() + other.getSizeX())
        {
            //check the Y axis
            if(Math.abs(getY() - other.getY()) < getSizeY() + other.getSizeY())
            {
                //check the Z axis
                if(Math.abs(getZ() - other.getZ()) < getSizeZ() + other.getSizeZ())
                {
                    return true;
                }
            }
        }

        return false;
    }
    public float getX(){
        return x;
    }
    public  float getY(){
        return y;
    }
    public float getZ(){
        return z;
    }
    public float getSizeX(){
        return sizeX;
    }
    public float getSizeY(){
        return sizeY;
    }
    public float getSizeZ(){
        return sizeZ;
    }

}
