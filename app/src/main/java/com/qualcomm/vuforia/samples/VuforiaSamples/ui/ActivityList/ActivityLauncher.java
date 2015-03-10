/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.qualcomm.vuforia.samples.VuforiaSamples.R;
import com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets.GameLobby;
import com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets.ObjTargets;


// This activity starts activities which demonstrate the Vuforia features
public class ActivityLauncher extends Activity
{
    
    private String mActivities[] = { "Create track","GameLobby","Tutorial"};
    private ImageView background;
    private Button newTrackBtn;
    private Button tutorialBtn;
    private Button joinLobbyBtn;
    private View.OnClickListener btnHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_launcher);


        background = (ImageView) findViewById(R.id.imageView);
        background.setImageResource(R.drawable.menu);

        newTrackBtn = (Button) findViewById(R.id.newTackBtn);
        joinLobbyBtn = (Button) findViewById(R.id.joinLobbyBtn);
        tutorialBtn = (Button) findViewById(R.id.tutorialBtn);

        btnHandler = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.newTackBtn:
                        Intent createGame = new Intent(ActivityLauncher.this, ObjTargets.class);
                        startActivity(createGame);
                        break;
                    case R.id.joinLobbyBtn:
                        Intent gameLobby = new Intent(ActivityLauncher.this, GameLobby.class);
                        startActivity(gameLobby);
                        break;
                    case R.id.tutorialBtn:
                        Log.d("ActivityLauncher","Not implemented");
                        break;
                }

            }
        };
        newTrackBtn.setOnClickListener(btnHandler);
        joinLobbyBtn.setOnClickListener(btnHandler);
        tutorialBtn.setOnClickListener(btnHandler);


    }
}
