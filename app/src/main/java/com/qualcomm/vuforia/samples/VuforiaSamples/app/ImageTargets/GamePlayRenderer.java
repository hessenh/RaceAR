package com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets;

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
import com.qualcomm.vuforia.samples.VuforiaSamples.network.CarPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Hans-Olav on 04.03.2015.
 */

// The renderer class for the ImageTargets sample.
public class GamePlayRenderer implements GLSurfaceView.Renderer {
    private static final String LOGTAG = "GamePlayRenderer";
    private final ArrayList<Float> xPath;
    private final ArrayList<Float> yPath;
    private final ArrayList<Integer> rPath;
    private final long startTime;
    private ArrayList<ObjObject> objectList;
    private final TrackData data;

    private SampleApplicationSession vuforiaAppSession;
    private GamePlay mActivity;

    private Vector<Texture> mTextures;

    private int shaderProgramID;
    private int counter = 1;
    private int vertexHandle;

    private int normalHandle;

    private int textureCoordHandle;

    private int mvpMatrixHandle;

    private int texSampler2DHandle;


    private SampleApplication3DModel mBuildingsModel;

    private Renderer mRenderer;

    boolean mIsActive = false;

    private ObjObject mObject;

    private int textureIndex;
    private int numberOfIdecies;
    private int numberOfTextures;
    private int indiceCounter;
    private float objectScale = 10;
    private ArrayList<String> partNames;
    private float turn =0;
    private long updateTime;
    private double carSpeed = 0;
    private final double carSpeedSlow =0.3;
    private final double carSpeedFast = 1;
    private int tCounter;
    private double distanceToTrack;
    private double maxDistance = 50;
    private ArrayList<Integer> traveledPath;
    private boolean winner = false;

    private float xRot,yRot = 0.0f;
    private float zRot = 1.0f;
    private boolean startGame = false;


    public GamePlayRenderer(GamePlay activity,SampleApplicationSession session) {
        mActivity = activity;
        vuforiaAppSession = session;

        //The data about the track
        data = TrackData.getInstance();
        //The list of parts
        partNames = data.getPartPath();
        xPath = data.getxPath();
        yPath = data.getyPath();
        rPath = data.getRotationPath();

        //Add the car to the track
        addCar();
        addOpponentCar();

        //Add the different parts to the objectlist
        addObjectsToList();


        startTime = System.currentTimeMillis();
    }
    private void addCar(){
        objectList = new ArrayList<ObjObject>();
        objectList.add(getPart("carone",xPath.get(0),yPath.get(0),90,0));
        traveledPath = new ArrayList<Integer>();
    }
    private void addOpponentCar(){
        objectList.add(getPart("carone2",xPath.get(0),yPath.get(0),90,0));
    }

    private void addObjectsToList() {
        if(partNames==null){
            objectList.add(getPart("turn", 50,0,0,2));
            objectList.add(getPart("turn", 50,50,90,3));
            objectList.add(getPart("turn", 0,50,180,4));
            objectList.add(getPart("turn", 0,0,270,5));
        }
        else{
            //Adding all the parts to the list
            for (int i = 0; i < partNames.size(); i++) {
                objectList.add(getPart(partNames.get(i), xPath.get(i), yPath.get(i),rPath.get(i),i+2));
            }

        }
    }

    private ObjObject getPart(String partName, float x, float y,int r,int id) {
        ObjObject part = new ObjObject(mActivity, partName, objectScale, r);
        part.setX(x);
        part.setY(y);
        part.setRotation(r);
        part.setID(id);
        return part;
    }

    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mIsActive)
            return;
        updateCarPosition();
        // Call our function to render content
        renderFrame();
    }

    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

        initRendering();

        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();
    }


    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);
    }


    // Function for initializing the renderer.
    private void initRendering() {

        mRenderer = Renderer.getInstance();

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);

        for (Texture t : mTextures) {
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

        try {
            mBuildingsModel = new SampleApplication3DModel();
            mBuildingsModel.loadModel(mActivity.getResources().getAssets(),
                    "ImageTargets/Buildings.txt");
        } catch (IOException e) {
            Log.e(LOGTAG, "Unable to load buildings");
        }

        // Hide the Loading Dialog
        mActivity.loadingDialogHandler
                .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

    }

    private void initTextureInformation(ObjObject mObject) {
        indiceCounter = 0;
        tCounter = 0;
        numberOfTextures = mObject.getTextureNames().size();
        textureIndex = mObject.getTextureIndex(0);
        //textureIndex = 0;
        String textureName = mObject.getTextureNames().get(0);
        numberOfIdecies = mObject.getTextureMap().get(textureName);
    }

    private void updateTextureInformation(ObjObject mObject, int counter) {
        indiceCounter += numberOfIdecies;
        if(counter<numberOfTextures){
            textureIndex = mObject.getTextureIndex(counter);
            String textureName = mObject.getTextureNames().get(counter);
            numberOfIdecies = mObject.getTextureMap().get(textureName);
        }
    }


    // The render function.
    private void renderFrame() {
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

            for (int i = 0; i < objectList.size(); i++) {
                //Setting the render for that part
                mObject = objectList.get(i);
                initTextureInformation(mObject);

                TrackableResult result = state.getTrackableResult(tIdx);
                Trackable trackable = result.getTrackable();
                printUserData(trackable);

                Matrix44F modelViewMatrix_Vuforia = Tool
                        .convertPose2GLMatrix(result.getPose());

                float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

               if(mObject.getPartName().equals("winner") || mObject.getPartName().equals("loser")){
                   mObject.setZ(100);
                    Matrix.setIdentityM(modelViewMatrix,0);
                    xRot = +0.01f;
                }

                Matrix.translateM(modelViewMatrix, 0, objectList.get(i).getX(), objectList.get(i).getY(), objectList.get(i).getZ());

                Matrix.rotateM(modelViewMatrix, 0, mObject.getRotation(), xRot, yRot, zRot);

                Matrix.scaleM(modelViewMatrix, 0, objectScale, objectScale, objectScale);


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


                for (int j = 0; j < mObject.getTextureNames().size(); j++) {

                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                            mTextures.get(textureIndex).mTextureID[0]);

                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, numberOfIdecies + indiceCounter, GLES20.GL_UNSIGNED_SHORT, mObject.getIndices());

                    //Update the texture
                    updateTextureInformation(mObject, j+1);
                    /*if (tCounter < numberOfTextures) {
                        updateTextureInformation(mObject, j);

                    } else {
                        initTextureInformation(mObject);
                        // disable the enabled arrays
                    }*/

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


    private void printUserData(Trackable trackable) {
        String userData = (String) trackable.getUserData();
        //Log.d(LOGTAG, "UserData:Retreived User Data	\"" + userData + "\"");
    }


    public void setTextures(Vector<Texture> textures) {
        mTextures = textures;

    }


    public void resetGame() {
        objectList.clear();
    }

    public void updateCarPosition(){
        if(System.currentTimeMillis()-updateTime>20 && !winner && startGame){
            ObjObject car = objectList.get(0);
            updateTime = System.currentTimeMillis();
            objectList.get(0).setRotation((int) (car.getRotation() - turn));

            float x  = (float) (Math.cos(Math.toRadians(objectList.get(0).getRotation()))*carSpeed);
            float y = (float) (Math.sin(Math.toRadians(objectList.get(0).getRotation()))*carSpeed);
            objectList.get(0).setX( car.getX() + x);
            objectList.get(0).setY(car.getY() + y);

            //Find closest object
            int closestPart = getClosestPart(car);
            //Check if winner
            winner = isWinner(closestPart);

            if(distanceToTrack>maxDistance/2){
                carSpeed = carSpeedSlow;
                if(distanceToTrack>maxDistance){
                    //If the object is too far away from the closest object, move the object to the track
                    objectList.get(0).setX(objectList.get(closestPart).getX());
                    objectList.get(0).setY(objectList.get(closestPart).getY());
                    objectList.get(0).setRotation(objectList.get(closestPart).getRotation());
                    carSpeed = carSpeedFast;
                }
            }
            //Set the speed to be normal
            else {
                carSpeed = carSpeedFast;
            }
        }
    }

    private int getClosestPart(ObjObject car) {
        //Two first objects are the cars, so have to start at 2
        int pos = 2;
        double d = car.getDistance(objectList.get(2));
        for(int i=3;i<objectList.size();i++){
            if(car.getDistance(objectList.get(i))<d){
                pos = i;
                d = car.getDistance(objectList.get(i));
            }
        }
        distanceToTrack = d;
        return pos;
    }
    private boolean isWinner(int pos){
        if(!traveledPath.contains(pos)){
            System.out.println("Adding pos: " + pos);
            traveledPath.add(pos);
        }
       // System.out.println(pos + " " + traveledPath.size() + " " + objectList.size() +" "+ traveledPath.get(0));
        //If the number of traveled parts is equal to the number of parts and the new part is the first part of the track
        if(traveledPath.size()>=objectList.size()-3 && traveledPath.get(0) == pos){
            Log.d("GamePlayRenderer","Winning");
            startCelebration();
            return true;
        }
        return false;
    }

    private void startCelebration() {
        objectList.add(getPart("winner",0,0,180,0));
    }
    public void startOtherCelebration(){
        objectList.add(getPart("loser",0,0,180,0));
    }

    public void updateOpponentCar(CarPacket carPacket){
        objectList.get(1).setX(carPacket.getX());
        objectList.get(1).setY(carPacket.getY());
        objectList.get(1).setRotation(carPacket.getAngle());
    }

    public CarPacket getCarPacket() {
        CarPacket packet = new CarPacket(
                objectList.get(0).getX(),
                objectList.get(0).getY(),
                objectList.get(0).getRotation()
                );
        return packet;
    }

    public void setTurnValue(float turnValue) {
        this.turn = turnValue;
    }

    public void startCar() {
        startGame = true;
    }
}

