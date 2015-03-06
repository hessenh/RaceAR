/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.qualcomm.QCAR.QCAR;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.Vec3F;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.qualcomm.vuforia.samples.SampleApplication.utils.CubeShaders;
import com.qualcomm.vuforia.samples.SampleApplication.utils.LoadingDialogHandler;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleApplication3DModel;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleMath;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleUtils;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Teapot;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Texture;


// The renderer class for the ImageTargets sample. 
public class ImageTargetRenderer implements GLSurfaceView.Renderer
{
    private static final String LOGTAG = "ImageTargetRenderer";
    private final ArrayList<Teapot> teapots;

    private SampleApplicationSession vuforiaAppSession;
    private ImageTargets mActivity;

    private Vector<Texture> mTextures;

    private int shaderProgramID;
    private int counter;
    private int vertexHandle;

    private int normalHandle;

    private int textureCoordHandle;

    private int mvpMatrixHandle;

    private int texSampler2DHandle;

    private Teapot mTeapot;

    private float kBuildingScale = 12.0f;
    private SampleApplication3DModel mBuildingsModel;

    private Renderer mRenderer;

    boolean mIsActive = false;

    private static final float OBJECT_SCALE_FLOAT = 3.0f;
    private Teapot mTeapot2;

    private ArrayList<BoundingBox> boundingBoxes;
    private float[] points;
    private float x;
    private float y;
    private float z;
    private boolean lifting = false;
    private long timestap;


    public ImageTargetRenderer(ImageTargets activity,
                               SampleApplicationSession session)
    {
        mActivity = activity;
        vuforiaAppSession = session;
        boundingBoxes = new ArrayList<BoundingBox>();
        teapots = new ArrayList<Teapot>();
        setFourTeapots();


    }
    private void setFourTeapots(){

        Teapot t1 = new Teapot(0);
        t1.setX(-100);
        t1.setY(80);
        t1.setZ(0);
        t1.init();
        teapots.add(t1);

        Teapot t2 = new Teapot(1);
        t2.setX(-30);
        t2.setY(80);
        t2.setZ(0);
        t2.init();
        teapots.add(t2);

        Teapot t3 = new Teapot(2);
        t3.setX(30);
        t3.setY(80);
        t3.setZ(0);
        t3.init();
        teapots.add(t3);

        Teapot t4 = new Teapot(3);
        t4.setX(100);
        t4.setY(80);
        t4.setZ(0);
        t4.init();
        teapots.add(t4);
        counter = 4;
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
        mTeapot2 = new Teapot(100);
        mTeapot = new Teapot(101);

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


    // The render function.
    private void renderFrame()
    {
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

            for(int i=0;i<teapots.size();i++){


                TrackableResult result = state.getTrackableResult(tIdx);
                Trackable trackable = result.getTrackable();
                printUserData(trackable);

                Matrix44F modelViewMatrix_Vuforia = Tool
                        .convertPose2GLMatrix(result.getPose());

                float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

                float ax = (float) Math.atan2(modelViewMatrix[6],modelViewMatrix[10]);
                float ay = (float) Math.atan2(-modelViewMatrix[2],Math.sqrt(modelViewMatrix[6]*modelViewMatrix[6] + modelViewMatrix[10]*modelViewMatrix[10]));
                float az = (float) Math.atan2(modelViewMatrix[1],modelViewMatrix[0]);
                //System.out.println(ax + " and "+ ay  + " and " + az);

                x = modelViewMatrix[13];
                y = modelViewMatrix[12];
               /* y = -(modelViewMatrix[12]*modelViewMatrix[0] + modelViewMatrix[12]*modelViewMatrix[4] + modelViewMatrix[12]*modelViewMatrix[8]);
                x = -(modelViewMatrix[13]*modelViewMatrix[1]+modelViewMatrix[13]*modelViewMatrix[5]+modelViewMatrix[13]*modelViewMatrix[9]);

                float [] test = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                Matrix.invertM(test,0,modelViewMatrix,0);
                printMatrix(test);*/

                z = modelViewMatrix[14];

                //alcprintMatrix(modelViewMatrix);

                if(teapots.get(i).pointIntersects(x,y,z) && !lifting && System.currentTimeMillis()-timestap>1000){
                    System.out.println("Grabbed: " + teapots.get(i).getID());
                    teapots.get(i).setFloating(true);
                    lifting = true;
                    timestap = System.currentTimeMillis();
                }
                if(teapots.get(i).isFloating() && lifting){

                    if(z<80 && !checkCollision(x,y,teapots.get(i)) && (System.currentTimeMillis()-timestap)>1000){
                        teapots.get(i).setX(x);
                        teapots.get(i).setY(y);
                        teapots.get(i).setZ(0);
                        teapots.get(i).setFloating(false);
                        lifting = false;
                        timestap = System.currentTimeMillis();
                    }
                    else{
                        Matrix.setIdentityM(modelViewMatrix,0);
                    }

                }
                Matrix.translateM(modelViewMatrix,0,teapots.get(i).getX(),teapots.get(i).getY(),teapots.get(i).getZ());

                int textureIndex = trackable.getName().equalsIgnoreCase("stones") ? 0
                        : 1;
                textureIndex = trackable.getName().equalsIgnoreCase("tarmac") ? 2
                        : textureIndex;


                // deal with the modelview and projection matrices
                float[] modelViewProjection = new float[16];

                Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession.getProjectionMatrix().getData(), 0, modelViewMatrix, 0);

                // activate the shader program and bind the vertex/normal/tex coords
                GLES20.glUseProgram(shaderProgramID);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                        false, 0, mTeapot.getVertices());
                GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                        false, 0, mTeapot.getNormals());
                GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                        GLES20.GL_FLOAT, false, 0, mTeapot.getTexCoords());

                GLES20.glEnableVertexAttribArray(vertexHandle);
                GLES20.glEnableVertexAttribArray(normalHandle);
                GLES20.glEnableVertexAttribArray(textureCoordHandle);

                // activate texture 0, bind it, and pass to shader
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                        mTextures.get(textureIndex).mTextureID[0]);
                GLES20.glUniform1i(texSampler2DHandle, 0);

                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

                // finally draw the teapot
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, mTeapot2.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT, mTeapot2.getIndices());


                // disable the enabled arrays
                GLES20.glDisableVertexAttribArray(vertexHandle);
                GLES20.glDisableVertexAttribArray(normalHandle);
                GLES20.glDisableVertexAttribArray(textureCoordHandle);


                SampleUtils.checkGLError("Render Frame");
            }

        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        mRenderer.end();
    }

    private void printMatrix(float[] modelViewMatrix) {
        System.out.println(modelViewMatrix[0] + ", " + modelViewMatrix[4] + ", " + modelViewMatrix[8] + ", " + modelViewMatrix[12]);
        System.out.println(modelViewMatrix[1] + ", " + modelViewMatrix[5] + ", " + modelViewMatrix[9] + ", " + modelViewMatrix[13]);
        System.out.println(modelViewMatrix[2] + ", " + modelViewMatrix[6] + ", " + modelViewMatrix[10] + ", " + modelViewMatrix[14]);
        System.out.println(modelViewMatrix[3] + ", " + modelViewMatrix[7] + ", " + modelViewMatrix[11] + ", " + modelViewMatrix[15]);
        System.out.println("______________________-");

    }

    private boolean checkCollision(float x,float y,Teapot teapot) {
        for(int i = 0;i<teapots.size();i++){
            if(teapot.collides(teapots.get(i),x,y)){
                return true;
            }
        }
        return false;


    }


    private void drawTeapot(int textureIndex, float[] modelViewProjection, Teapot teapot,Matrix44F modelView){



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

    public void addTeapot() {
        Teapot teapot = new Teapot(counter);
        teapot.init();
        teapot.setX(x);
        teapot.setY(y);
        teapot.setZ(0);
        boolean temp = true;
        for(int i=0;i<teapots.size();i++){
            if(teapot.collides(teapots.get(i),x,y)){
                temp = false;
            }
        }
        if(temp){
            teapots.add(teapot);
            counter ++;
        }


    }
}
