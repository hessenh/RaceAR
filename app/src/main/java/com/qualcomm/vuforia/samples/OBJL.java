package com.qualcomm.vuforia.samples;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OBJL {
    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;

    private int indicesNumber = 0;
    private int verticesNumber = 0;
    private Context context;
    private double[] OBJECT_VERTS;
    private int vCounter = 0;

    private int nomalsNumber;
    private double[] OBJECT_NORMS;
    private int nCounter = 0;
    private short[] OBJECT_INDICES;
    private int iCounter = 0;
    private int vtNumber;
    private double[] OBJECT_TEX_COORDS;
    private int vtCounter = 0;

    private ArrayList<Short> vertex_i;
    private ArrayList<Integer> texture_i;
    private ArrayList<Integer> normal_i;
    private double[] OBJECT_NORMS2;
    private double[] OBJECT_TEX_COORDS2;
    private Map<String,Integer> textureMap;
    private ArrayList<String> textureNames;
    private int step;
    private String texture;


    public OBJL(Context context,String file){
        this.context = context;
        vertex_i = new ArrayList<Short>();
        normal_i = new ArrayList<Integer>();
        texture_i = new ArrayList<Integer>();

        textureMap = new HashMap<String, Integer>();
        textureNames = new ArrayList<String>();

        System.out.println(file + ".obj");
        ReadFromfile(file + ".obj");

        /*for(int i=0;i<textureNames.size();i++){
            System.out.println(textureNames.get(i));
        }
        for (String key : textureMap.keySet()) {
            System.out.println(key  + " with " + textureMap.get(key));
        }*/


        convertVertexToArray();
        indicesNumber = vertex_i.size();
        //converTextureToArray();
        //converNormToArray();

        /*System.out.println("Vericesnumber "  + verticesNumber);
        System.out.println("IndicesNumber " + indicesNumber);
        System.out.println("NormalNumber " + nomalsNumber);
        System.out.println("TexturNumber " + vtNumber);*/
    }

    private void converNormToArray() {
        OBJECT_NORMS2 = new double[normal_i.size()*3];

        for(int i=0;i<normal_i.size()-2; i++) {
            OBJECT_NORMS2[i*3] = OBJECT_NORMS[normal_i.get(i)];
            OBJECT_NORMS2[i*3+1] = OBJECT_NORMS[normal_i.get(i+1)];
            OBJECT_NORMS2[i*3+2] = OBJECT_NORMS[normal_i.get(i+2)];
        }
        nomalsNumber = OBJECT_NORMS2.length;
    }

    private void converTextureToArray() {
        OBJECT_TEX_COORDS2 = new double[texture_i.size()*3];

        for(int i=0;i<texture_i.size()-2; i++) {
            OBJECT_TEX_COORDS2[i*3] = OBJECT_TEX_COORDS[texture_i.get(i)];
            OBJECT_TEX_COORDS2[i*3+1] = OBJECT_TEX_COORDS[texture_i.get(i+1)];
            OBJECT_TEX_COORDS2[i*3+2] = OBJECT_TEX_COORDS[texture_i.get(i+2)];
        }
        vtNumber = OBJECT_TEX_COORDS2.length;

    }

    private void convertVertexToArray() {
        OBJECT_INDICES = new short[vertex_i.size()];
        for(int i =0;i< vertex_i.size();i++){
            OBJECT_INDICES[i] = vertex_i.get(i);
        }
    }


    public void ReadFromfile(String fileName) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                parseLine(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
    }

    private void parseLine(String line) {
        if(line.startsWith("#") && line.contains("vertex positions")){
            String[] v = line.split(" ");
            verticesNumber = Integer.parseInt(v[1])*3;
            OBJECT_VERTS = new double[verticesNumber];
        }
        if(line.startsWith("v ")){
            String[] v = line.split(" ");
            OBJECT_VERTS[vCounter] = Double.parseDouble(v[2]);
            vCounter++;
            OBJECT_VERTS[vCounter] = Double.parseDouble(v[3]);
            vCounter++;
            OBJECT_VERTS[vCounter] = Double.parseDouble(v[4]);
            vCounter++;
        }
        if(line.startsWith("#") && line.contains("UV coordinates")){
            String[] v = line.split(" ");
            vtNumber = Integer.parseInt(String.valueOf(v[1]))*3;
            OBJECT_TEX_COORDS = new double[vtNumber];
        }
        if(line.startsWith("vt")){
            String[] v = line.split(" ");
            OBJECT_TEX_COORDS[vtCounter] = Double.parseDouble(v[1]);
            vtCounter++;
            OBJECT_TEX_COORDS[vtCounter] = Double.parseDouble(v[2]);
            vtCounter++;
        }

        if(line.startsWith("#") && line.contains("vertex normals")){
            String[] v = line.split(" ");
            nomalsNumber = Integer.parseInt(String.valueOf(v[1]))*3;
            OBJECT_NORMS = new double[nomalsNumber];
        }
        if(line.startsWith("vn")){
            String[] v = line.split(" ");
            OBJECT_NORMS[nCounter] = Double.parseDouble(v[1]);
            nCounter++;
            OBJECT_NORMS[nCounter] = Double.parseDouble(v[2]);
            nCounter++;
            OBJECT_NORMS[nCounter] = Double.parseDouble(v[3]);
            nCounter++;
        }
        /*if(line.startsWith("#") && line.contains("faces")){
            String[] v = line.split(" ");
            indicesNumber = Integer.parseInt(String.valueOf(v[1]))*3;
            OBJECT_INDICES = new short[indicesNumber];
        }*/
        if(line.startsWith("usemtl")){
            String[] v = line.split(" ");
            step=0;
            texture = v[1];
            textureNames.add(v[1]);
        }
        if(line.startsWith("f")){
            step+=3;
            textureMap.put(texture,step);
            String[] v = line.replace("/"," ").split(" ");
            vertex_i.add((short) (Short.valueOf(v[2]) - 1));
            vertex_i.add((short) (Short.valueOf(v[5]) - 1));
            vertex_i.add((short) (Short.valueOf(v[8]) - 1));
            //System.out.println(v[2] + " " +v[3] + " " +v[4] + " " +v[5] + " " +v[6] + " " +v[7] + " " +v[2] + " " +v[8] + " " +v[9] + " "+ v[10] + " ");
            texture_i.add((Integer.valueOf(v[3])-1));
            texture_i.add(Integer.valueOf(v[6])-1);
            texture_i.add(Integer.valueOf(v[9])-1);

            normal_i.add((Integer.valueOf(v[4])-1));
            normal_i.add((Integer.valueOf(v[7])-1));
            normal_i.add((Integer.valueOf(v[10])-1));


            /*OBJECT_INDICES[iCounter] = (short) (Short.parseShort(v[2]) -1);
            iCounter++;
            OBJECT_INDICES[iCounter] = (short) (Short.parseShort(v[5]) -1);
            iCounter++;
            OBJECT_INDICES[iCounter] = (short) (Short.parseShort(v[8]) -1);
            iCounter++;*/
        }
    }

    public double[] getVerts(){
        return OBJECT_VERTS;
    }
    public double[] getNorms(){
        return OBJECT_NORMS;
    }
    public short[] getIndices(){
        return OBJECT_INDICES;
    }
    public double[] getTexCoords(){
        return OBJECT_TEX_COORDS;
    }

    public int getVertsNumber() {
        return verticesNumber;
    }

    public int getIndicesNumber() {
        return indicesNumber;
    }
    public ArrayList<String> getTextureNames(){
        return textureNames;
    }
    public Map<String, Integer> getTextureMap(){
        return textureMap;
    }
}
