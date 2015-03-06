package com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets;

import android.content.Context;

import com.qualcomm.vuforia.samples.OBJL;
import com.qualcomm.vuforia.samples.SampleApplication.utils.MeshObject;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Teapot;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Hans-Olav on 27.02.2015.
 */
public class FigureObject extends MeshObject {


    private final Map<String, Integer> textureMap;
    private final ArrayList<String> textureNames;
    private OBJL obj;

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
    private float size;
    private int ID;
    private boolean liftup = false;

    public FigureObject(int id,Context context)
    {
        obj = new OBJL(context,"figure");

        mVertBuff = fillBuffer(obj.getVerts());
        mTexCoordBuff = fillBuffer(obj.getTexCoords());
        mNormBuff = fillBuffer(obj.getNorms());
        mIndBuff = fillBuffer(obj.getIndices());

        verticesNumber = obj.getVertsNumber();
        indicesNumber = obj.getIndicesNumber();

        textureMap = obj.getTextureMap();
        textureNames = obj.getTextureNames();

        this.size = 40;
        this.ID = id;
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
    public float getSize(){return size; }

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
        if(getX()<= x && x <=(getX()+getSize())){
            if(getY()<=y && y<=(getY()+getSize())){
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

    public boolean collides(Teapot other,float thisX,float thisY){
        //Not check with self
        if(getID() == other.getID()){
            return false;
        }
        //check the X axis
        if(Math.abs(thisX - other.getX()) < getSize())
        {
            //check the Y axis
            if(Math.abs(thisY - other.getY()) < getSize())
            {
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


}
