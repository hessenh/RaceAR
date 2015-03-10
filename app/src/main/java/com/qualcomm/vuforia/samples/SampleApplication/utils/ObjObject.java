/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.qualcomm.vuforia.samples.SampleApplication.utils;

import android.util.Log;

import com.qualcomm.vuforia.samples.OBJL;
import com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets.GamePlay;
import com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets.ObjTargets;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Map;


public class ObjObject extends MeshObject
{

    private final String partName;
    private ArrayList<String> textureNames;
    private Map<String, Integer> textureMap;
    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;

    private int indicesNumber = 0;
    private int verticesNumber = 0;
    private float y;
    private float x;
    private float z;
    private boolean first = true;
    private boolean floating;
    private float sizeX;
    private float sizeY;
    private float sizeZ;
    private int ID;
    private boolean liftup = false;

    private ObjObject westObject;
    private ObjObject eastObject;
    private ObjObject northObject;
    private ObjObject southObject;
    public boolean west,east,south,north;
    private int[] textureList;
    private int rotation;

    public ObjObject(ObjTargets context, String name, float objectScale,int rotationAngle)
    {
        OBJL obj = new OBJL(context,name.toLowerCase());

        mVertBuff = fillBuffer(obj.getVerts());
        mTexCoordBuff = fillBuffer(obj.getTexCoords());
        mNormBuff = fillBuffer(obj.getNorms());
        mIndBuff = fillBuffer(obj.getIndices());

        verticesNumber = obj.getVertsNumber();
        indicesNumber = obj.getIndicesNumber();

        textureMap = obj.getTextureMap();
        textureNames = obj.getTextureNames();

        this.partName = name.toLowerCase();
        this.rotation = rotationAngle;

        if (partName.equals("straight")) {
            textureList = new int[]{0, 1};
            sizeX = 5 * objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
            west = true;
            east = true;
            south = false;
            north = false;
            rotateObject(rotationAngle);
        }
        else if(partName.equals("tunnel")) {
            textureList = new int[]{0, 1, 4};
            sizeX = 5 * objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
            west = true;
            east = true;
            south = false;
            north = false;
            rotateObject(rotationAngle);
        }
        else if(partName.equals("turn")){
            textureList = new int[]{0, 1};
            sizeX = 5 * objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
            west = true;
            east = false;
            south = false;
            north = true;
            rotateObject(rotationAngle);
        }
        else if(partName.equals("marker")){
            System.out.println("Created new marker!");
            textureList = new int[]{2};
            sizeX = 5 * objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
            west = false;
            east = false;
            south = false;
            north = false;
        }else if(partName.equals("small")){
            System.out.println("Created new smallbox!");
            textureList = new int[]{3,0};
            sizeX = 1 * objectScale;
            sizeY = 1 * objectScale;
            sizeZ = 1 * objectScale;
            west = false;
            east = false;
            south = false;
            north = false;
        }
        else {
            textureList = new int[]{0, 1, 2};
            sizeX = 5 *objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
        }
    }
    public ObjObject(GamePlay context, String name, float objectScale,int rotationAngle)
    {
        OBJL obj = new OBJL(context,name.toLowerCase());

        mVertBuff = fillBuffer(obj.getVerts());
        mTexCoordBuff = fillBuffer(obj.getTexCoords());
        mNormBuff = fillBuffer(obj.getNorms());
        mIndBuff = fillBuffer(obj.getIndices());

        verticesNumber = obj.getVertsNumber();
        indicesNumber = obj.getIndicesNumber();

        textureMap = obj.getTextureMap();
        textureNames = obj.getTextureNames();

        this.partName = name.toLowerCase();
        this.rotation = rotationAngle;

        if (partName.equals("straight")) {
            textureList = new int[]{0, 1};
            sizeX = 5 * objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
            west = true;
            east = true;
            south = false;
            north = false;
            rotateObject(rotationAngle);
        }
        else if(partName.equals("carone")) {
            textureList = new int[]{5,6,7};
            sizeX = 5 * objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
            west = false;
            east = false;
            south = false;
            north = false;
            rotateObject(rotationAngle);
        }
        else if(partName.equals("carone2")) {
            textureList = new int[]{8,6,7};
            sizeX = 5 * objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
            west = false;
            east = false;
            south = false;
            north = false;
            rotateObject(rotationAngle);
        }
        else if(partName.equals("tunnel")) {
            textureList = new int[]{0, 1, 4};
            sizeX = 5 * objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
            west = true;
            east = true;
            south = false;
            north = false;
            rotateObject(rotationAngle);
        }
        else if(partName.equals("turn")){
            textureList = new int[]{0, 1};
            sizeX = 5 * objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
            west = true;
            east = false;
            south = false;
            north = true;
            rotateObject(rotationAngle);
        }
        else if(partName.equals("marker")){
            System.out.println("Created new marker!");
            textureList = new int[]{2};
            sizeX = 5 * objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
            west = false;
            east = false;
            south = false;
            north = false;
        }else if(partName.equals("small")){
            System.out.println("Created new smallbox!");
            textureList = new int[]{3,0};
            sizeX = 1 * objectScale;
            sizeY = 1 * objectScale;
            sizeZ = 1 * objectScale;
            west = false;
            east = false;
            south = false;
            north = false;
        }
        else {
            textureList = new int[]{0, 1, 2};
            sizeX = 5 *objectScale;
            sizeY = 5 * objectScale;
            sizeZ = 5 * objectScale;
        }
    }

    public int getNumObjectIndex()
    {
        return indicesNumber;
    }


    @Override
    public int getNumObjectVertex()
    {
        return verticesNumber;
    }


    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType)
    {
        Buffer result = null;
        switch (bufferType)
        {
            case BUFFER_TYPE_VERTEX:
                result = mVertBuff;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = mTexCoordBuff;
                break;
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
                break;
            case BUFFER_TYPE_INDICES:
                result = mIndBuff;
            default:
                break;

        }

        return result;
    }

    public void setX(float x){
        this.x = x;
    }
    public void setY(float y){
        this.y = y;
    }
    public void setZ(float z){
        this.z = z;
    }
    public float getX(){
        if(isFloating()){
            return 0;
        }
        return this.x;
    }
    public float getY(){
        if(isFloating()){
            return 0;
        }
        return y;
    }
    public float getZ(){
        if(isFloating()){
            return 100;
        }
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


    public boolean init() {
        if(first){
            first = false;
            return true;
        }
        return false;
    }

    public void setFloating(boolean b) {
        this.floating = b;
    }

    public boolean isFloating(){
        if(!isLiftUp()){
            liftup = true;
        }
        return this.floating;
    }

    public boolean pointIntersects(float x, float y,float z) {
        if(getX()<= x && x <=(getX()+getSizeX())){
            if(getY()<=y && y<=(getY()+getSizeY())){
                if(z<=100){
                    return true;
                }

            }
        }
        return false;

    }

    public int getID() {
        return this.ID;
    }
    public boolean isLiftUp(){
        return liftup;
    }

    public void setLiftUp(boolean b) {
        liftup = b;
    }

    public boolean collides(ObjObject other, float thisX, float thisY){
        //Not check with self
        if(getID() == other.getID()){
            return false;
        }
        //check the X axis
        if(Math.abs(thisX - other.getX()) < getSizeX())
        {
            //check the Y axis
            if(Math.abs(thisY - other.getY()) < getSizeY())
            {
                System.out.println(getID() + " with  " + other.getID());
                System.out.println("Collision with " + other.getID());
                System.out.println(other.getX() +" and " + thisX);
                return true;
            }
        }

        return false;
    }

    public ArrayList<String> getTextureNames(){
        return textureNames;
    }

    public Map<String, Integer> getTextureMap(){
        return textureMap;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ObjObject getWestObject() {
        return westObject;
    }

    public void setWestObject(ObjObject westObject) {
        this.westObject = westObject;
    }

    public ObjObject getEastObject() {
        return eastObject;
    }

    public void setEastObject(ObjObject eastObject) {
        this.eastObject = eastObject;
    }

    public ObjObject getNorthObject() {
        return northObject;
    }

    public void setNorthObject(ObjObject northObject) {
        this.northObject = northObject;
    }

    public ObjObject getSouthObject() {
        return southObject;
    }

    public void setSouthObject(ObjObject southObject) {
        this.southObject = southObject;
    }

    public String getPartName() {
        return partName;
    }

    public int getTextureIndex(int counter) {
        return textureList[counter];
    }

    public void setRotation(int rotation1) {
        rotation = rotation1;
        if(rotation>359){
            rotation=0;
        }
        if(rotation<0){
            rotation=359;
        }
        //Since the object has a set of possible neighbours, we have to change the boolean values according to the rotation
        rotateObject(rotation);
    }

    private void rotateObject(int angle) {
        if (partName.equals("straight")) {

            //System.out.println("Flipping straight");
            if(this.rotation==0 || this.rotation == 180){
                west = true;
                east = true;
                south = false;
                north = false;
            }
            else{
                west = false;
                east = false;
                south = true;
                north = true;
            }

        }
        else if(partName.equals("turn")){
            //System.out.println("Flipping turn!");
            if(this.rotation==0){
                west = true;
                east = false;
                south = false;
                north = true;
            }
            else if (this.rotation==90){
                west = true;
                east = false;
                south = true;
                north = false;
            }
            else if (this.rotation==180){
                west = false;
                east = true;
                south = true;
                north = false;
            }
            else if(this.rotation==270){
                west = false;
                east = true;
                south = false;
                north = true;
            }
        }
    }


    public int getRotation() {
        return rotation;
    }


    //Possible to find better way to do this...
    public void addNeighbours(ArrayList<ObjObject> objectList) {
        //Iterate over all objects(not marker) to set the neighbours
        for(int i=1;i<objectList.size();i++){
            //Object is left/westObject
            ObjObject other = objectList.get(i);

            if(other.getX()+getSizeX() == this.getX()){
                if(other.getY() == getY()){
                    if(west && other.getEastBoolean()){
                        //System.out.println(this.getID() + ": westObject is " + objectList.get(i).getID());
                        westObject = other;
                        objectList.get(i).setEastObject(this);
                    }
                }
            }
            //Object is right/eastObject
            if(other.getX()-getSizeX() == getX()){
                if(other.getY() == getY()){
                    if(east && other.getWestBoolean()){
                        //System.out.println(this.getID() + ": eastObject is " + objectList.get(i).getID());
                        eastObject = objectList.get(i);
                        objectList.get(i).setWestObject(this);
                    }
                }
            }
            //Object is up/northObject
            if(other.getX() == getX()){
                if(other.getY()-getSizeY() == getY()){
                    if(north && other.getSouthBoolean()){
                        //System.out.println(this.getID() + ": northObject is " + objectList.get(i).getID());
                        northObject = objectList.get(i);
                        objectList.get(i).setSouthObject(this);
                    }
                }
            }
            //Object is down/southObject
            if(other.getX() == getX()){
                if(other.getY()+getSizeY() == getY()){
                    if(south && other.getNorthBoolean()){
                        //System.out.println(this.getID() + ": southObject is " + objectList.get(i).getID());
                        southObject = objectList.get(i);
                        objectList.get(i).setNorthObject(this);
                    }
                }
            }
        }
    }

    public boolean getEastBoolean() {
        return east;
    }

    public boolean getWestBoolean() {
        return west;
    }

    public boolean getSouthBoolean() {
        return south;
    }

    public boolean getNorthBoolean() {
        return north;
    }

    //Returning the distance between two objects
    public double getDistance(ObjObject objObject) {
        return Math.sqrt(Math.pow(objObject.getX()-getX(),2) +Math.pow(objObject.getY()-getY(),2));
    }
}
