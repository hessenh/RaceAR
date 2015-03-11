package com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets;

/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.qualcomm.vuforia.samples.SampleApplication.utils.CubeShaders;
import com.qualcomm.vuforia.samples.SampleApplication.utils.LoadingDialogHandler;
import com.qualcomm.vuforia.samples.SampleApplication.utils.ObjObject;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleApplication3DModel;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleUtils;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Texture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


// The renderer class for the ImageTargets sample.
public class ObjTargetRenderer implements GLSurfaceView.Renderer
{
    private static final String LOGTAG = "ImageTargetRenderer";
    private final ArrayList<ObjObject> objectList;
    private final Map map;

    private SampleApplicationSession vuforiaAppSession;
    private ObjTargets mActivity;

    private Vector<Texture> mTextures;

    private int shaderProgramID;
    private int counter =1;
    private int vertexHandle;

    private int normalHandle;

    private int textureCoordHandle;

    private int mvpMatrixHandle;

    private int texSampler2DHandle;


    private float kBuildingScale = 12.0f;
    private SampleApplication3DModel mBuildingsModel;

    private Renderer mRenderer;

    boolean mIsActive = false;

    private static final float OBJECT_SCALE_FLOAT = 3.0f;
    private ObjObject mObject;

    private ArrayList<BoundingBox> boundingBoxes;
    private float[] points;
    private float x;
    private float y;
    private float z;
    private boolean lifting = false;
    private long timestap;
    private int textureIndex;
    private int numberOfIdecies;
    private int numberOfTextures;
    private int indiceCounter;
    private float angle;
    private String activePart = "";
    private boolean floatingPart = false;
    private ObjObject newObject;
    private ObjObject markerObject;
    private float objectScale = 10;
    private float newX;
    private float newY;
    private int rotationAngle;
    private boolean circuit = false;
    private ObjObject smallBox;


    public ObjTargetRenderer(ObjTargets activity,
                               SampleApplicationSession session)
    {
        mActivity = activity;
        vuforiaAppSession = session;
        objectList = new ArrayList<ObjObject>();

        //This object is the blue(if not changed) marker to show where the new object will be placed
        markerObject = new ObjObject(mActivity,"marker",objectScale,0);
        //Set the id to be the first object(0)
        markerObject.setID(0);
        //Add the marker to the rendering list
        objectList.add(markerObject);

        //The scale factor and the size of the object
        map = new Map(objectScale,5);


        smallBox = new ObjObject(mActivity,"small",objectScale,0);

    }

    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;

        // Call our function to render content
        renderFrame();
    }

    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

        initRendering();

        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();
    }


    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);
    }


    // Function for initializing the renderer.
    private void initRendering()
    {
        if(activePart==""){
            activePart = "turn";
        }
        mObject = new ObjObject(mActivity,activePart, objectScale,0);
        initTextureInformation(mObject);

        mRenderer = Renderer.getInstance();

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);

        for (Texture t : mTextures)
        {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                    t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE, t.mData);
        }

        shaderProgramID = SampleUtils.createProgramFromShaderSrc(
                CubeShaders.CUBE_MESH_VERTEX_SHADER,
                CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexPosition");
        normalHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexNormal");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
                "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
                "texSampler2D");

        try
        {
            mBuildingsModel = new SampleApplication3DModel();
            mBuildingsModel.loadModel(mActivity.getResources().getAssets(),
                    "ImageTargets/Buildings.txt");
        } catch (IOException e)
        {
            Log.e(LOGTAG, "Unable to load buildings");
        }

        // Hide the Loading Dialog
        mActivity.loadingDialogHandler
                .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

    }

    private void initTextureInformation(ObjObject mObject) {
        indiceCounter = 0;
        numberOfTextures = mObject.getTextureNames().size();
        textureIndex = mObject.getTextureIndex(0);
        String textureName = mObject.getTextureNames().get(0);
        numberOfIdecies = mObject.getTextureMap().get(textureName);
    }
    private void updateTextureInformation(ObjObject mObject,int counter){
        indiceCounter += numberOfIdecies;
        //textureIndex = counter;
        numberOfTextures = mObject.getTextureNames().size();
        textureIndex = mObject.getTextureIndex(counter);
        String textureName = mObject.getTextureNames().get(counter);
        numberOfIdecies = mObject.getTextureMap().get(textureName);
    }




    // The render function.
    private void renderFrame()
    {
        angle += 5;

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        State state = mRenderer.begin();
        mRenderer.drawVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // handle face culling, we need to detect if we are using reflection
        // to determine the direction of the culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            GLES20.glFrontFace(GLES20.GL_CW); // Front camera
        else
            GLES20.glFrontFace(GLES20.GL_CCW); // Back camera



        // did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {

            for(int i=0;i< objectList.size();i++){
                mObject = objectList.get(i);
                if(mObject.equals(smallBox)){
                    drawCircuit();
                }

                initTextureInformation(mObject);

                TrackableResult result = state.getTrackableResult(tIdx);
                Trackable trackable = result.getTrackable();
                printUserData(trackable);

                Matrix44F modelViewMatrix_Vuforia = Tool
                        .convertPose2GLMatrix(result.getPose());

                float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();



                x = -modelViewMatrix[12];
                y = modelViewMatrix[13];
                z = modelViewMatrix[14];


                if(mObject.isFloating()){
                    Matrix.translateM(modelViewMatrix,0, x, y, 0);
                }else {
                    Matrix.translateM(modelViewMatrix,0, objectList.get(i).getX(), objectList.get(i).getY(), objectList.get(i).getZ());
                }
                if(mObject.equals(markerObject)){
                    newX = Math.round(x/markerObject.getSizeX())*markerObject.getSizeX();
                    newY = Math.round(y/markerObject.getSizeY())*markerObject.getSizeY();

                    markerObject.setX(newX);
                    markerObject.setY(newY);
                }

                Matrix.rotateM(modelViewMatrix,0,mObject.getRotation(),0f,0f,1f);

                Matrix.scaleM(modelViewMatrix,0,objectScale,objectScale,objectScale);



                // deal with the modelview and projection matrices
                float[] modelViewProjection = new float[16];

                Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession.getProjectionMatrix().getData(), 0, modelViewMatrix, 0);

                // activate the shader program and bind the vertex/normal/tex coords
                GLES20.glUseProgram(shaderProgramID);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                        false, 0, mObject.getVertices());
                GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                        false, 0, mObject.getNormals());
                GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                        GLES20.GL_FLOAT, false, 0, mObject.getTexCoords());

                GLES20.glEnableVertexAttribArray(vertexHandle);
                GLES20.glEnableVertexAttribArray(normalHandle);
                GLES20.glEnableVertexAttribArray(textureCoordHandle);

                // activate texture 0, bind it, and pass to shader
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                        mTextures.get(textureIndex).mTextureID[0]);
                GLES20.glUniform1i(texSampler2DHandle, 0);

                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);


                for(int j=0;j<mObject.getTextureNames().size();j++){

                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                            mTextures.get(textureIndex).mTextureID[0]);

                    //System.out.println(numberOfIdecies + " and " + textureIndex + " with " + indiceCounter);
                    //System.out.println(mObject.getNumObjectIndex() + " number of object - and " + mObject.getIndices().position());

                    //mObject.getIndices().position(indiceCounter);

                    //GLES20.glDrawElements(GLES20.GL_TRIANGLES, numberOfIdecies, GLES20.GL_UNSIGNED_SHORT, mObject.getIndices().position(indiceCounter));
                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, numberOfIdecies+indiceCounter, GLES20.GL_UNSIGNED_SHORT, mObject.getIndices());

                    //Update the texture
                    if(textureIndex+1<numberOfTextures) {
                        updateTextureInformation(mObject, textureIndex + 1);

                    }
                    else{
                        initTextureInformation(mObject);
                        // disable the enabled arrays
                    }

                }


                GLES20.glDisableVertexAttribArray(vertexHandle);
                GLES20.glDisableVertexAttribArray(normalHandle);
                GLES20.glDisableVertexAttribArray(textureCoordHandle);


                SampleUtils.checkGLError("Render Frame");
            }

        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        mRenderer.end();
    }


    private void printUserData(Trackable trackable)
    {
        String userData = (String) trackable.getUserData();
        //Log.d(LOGTAG, "UserData:Retreived User Data	\"" + userData + "\"");
    }


    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;

    }

    public void addPart() {
        //Placing the
        if(!floatingPart && !circuit){
            newObject = new ObjObject(mActivity,getActivePart(),objectScale,rotationAngle);
            newObject.init();
            newObject.setX(x);
            newObject.setY(y);
            newObject.setZ(0);
            newObject.setID(counter);
            newObject.setFloating(true);

            floatingPart = true;
            objectList.add(newObject);
        }
    }
    public void gluePart(){
        //Actually placing the object on the maze
        if(floatingPart && !circuit){
            newObject = objectList.get(counter);

            //Check if the object collides with other objects
            boolean collision = false;
            for(int i=1;i< objectList.size();i++){
                if(newObject.collides(objectList.get(i),newX,newY)){
                    collision = true;
                }
            }
            //If no collisions
            if(!collision) {
                counter++;
                //This object is not floating anymore
                newObject.setFloating(false);
                //newX and  newY is step-values
                newObject.setX(newX);
                newObject.setY(newY);
                //No floating objects anymore
                floatingPart = false;
                //set neighbours
                newObject.addNeighbours(objectList);
                //Check if map is a circuit -> Ready to race
                if(map.isCircuit(objectList)){
                    circuit = true;
                    smallBox.setID(counter);
                    objectList.add(smallBox);
                    drawCircuit();
                    storeData();
                }
            }
        }
    }

    private void storeData() {
        TrackData trackData = TrackData.getInstance();
        trackData.setPartPath(map.getPartPath());
        trackData.setxPath(map.getxPath());
        trackData.setyPath(map.getyPath());
        trackData.setRotationPath(map.getRotationPath());
    }

    private void drawCircuit() {
        smallBox.setX(map.getCircuitPosX());
        smallBox.setY(map.getCircuitPosY());
        smallBox.setZ(map.getCircuitPosZ());
        smallBox.setZ(20);

    }

    //The active object is changed.
    private void changeActivePart(String activePart){
        //Remove floating part, replace it with new part
        if(objectList.size()-1==counter){
            objectList.remove(counter);
        }
        rotationAngle =0;
        newObject = new ObjObject(mActivity,activePart, objectScale,rotationAngle);
        newObject.init();
        newObject.setX(x);
        newObject.setY(y);
        newObject.setZ(0);
        newObject.setID(counter);
        newObject.setFloating(true);
        floatingPart = true;
        objectList.add(newObject);
    }
    //Setting the active part
    public void setActivePart(String activePart) {
        System.out.println("Active part is now " + activePart);
        this.activePart = activePart;
        changeActivePart(activePart);
    }

    public String getActivePart() {
        return activePart;
    }

    public void rotateObject() {
        if(newObject!=null && !circuit && floatingPart){
            rotationAngle+=90;
            if(rotationAngle>=360){
                rotationAngle = 0;
            }
            newObject.setRotation(rotationAngle);
        }



    }

    public void resetGame() {
        floatingPart = false;
        circuit = false;
        objectList.clear();
        objectList.add(markerObject);
        counter = 1;
    }

    public boolean isCircuit() {
        return circuit;
    }
}
