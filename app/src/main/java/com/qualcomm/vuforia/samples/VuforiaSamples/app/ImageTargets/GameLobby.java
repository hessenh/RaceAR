package com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.qualcomm.vuforia.samples.VuforiaSamples.R;
import com.qualcomm.vuforia.samples.VuforiaSamples.network.*;
import java.util.concurrent.ExecutionException;
import static com.qualcomm.vuforia.samples.VuforiaSamples.network.ClientPacket.ClientAction.*;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class GameLobby extends Activity implements PacketHandler {

    private Button mPlayBtn, mConnectBtn, mSendTrackBtn;
    private TextView mMyIP;
    private EditText mConnectIP;
    private final String ip = Config.getDottedDecimalIP(Config.getLocalIPAddress());
    private TextView status;
    Client mClient;
    private ImageView mBackgroundImage;
    private long lastTimeSync;
    private Clock clock;
    private boolean host = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);

        clock = Clock.getInstance();

        mBackgroundImage = (ImageView)findViewById(R.id.lobbyBg);
        mBackgroundImage.setImageResource(R.drawable.duellblur);

        mClient = new Client(this, ip);
        mClient.start();

        mPlayBtn = (Button) findViewById(R.id.discover);
        mConnectBtn = (Button)findViewById(R.id.connectBtn);
        mSendTrackBtn = (Button) findViewById(R.id.sendTrackBtn);
        status = (TextView)findViewById(R.id.textStatus);

        mMyIP = (TextView) findViewById(R.id.editIP);
        mConnectIP = (EditText) findViewById(R.id.editConnectIP);

        mMyIP.setFocusable(false);
        mMyIP.setText(ip);

        lastTimeSync = -1;

        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GameLobby", "SyncTime: " + String.valueOf(clock.getTime()));
                Intent gameplay = new Intent(getApplicationContext(), GamePlay.class);
                gameplay.putExtra("ip", ip);
                gameplay.putExtra("host",host);
                startActivity(gameplay);
            }
        });


        final OnClickListener mConnectListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String connectIP = mConnectIP.getText().toString();
                AsyncTask<String, Void, Boolean> task = new ConnectOperation();
                task.execute(connectIP);
                try {
                    if(task.get())
                        status.setText("Connected");
                } catch(ExecutionException e) {

                } catch(InterruptedException e) {

                }
            }
        };

        mConnectBtn.setOnClickListener(mConnectListener);

        mSendTrackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<String, Void, Boolean> task = new SendTrackOperation();
                task.execute();
                //Sending track = host
                host = true;
                try {
                    if(task.get())
                        status.setText("Track sent");
                } catch(ExecutionException e) {
                    Log.e("GameLobby", "mSendTrackBtn", e);

                } catch(InterruptedException e) {
                    Log.e("GameLobby", "mSendTrackBtn", e);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_lobby, menu);
        return true;
    }

    @Override
    public void carPacketHandler(CarPacket packet) {

    }

    @Override
    public void trackPacketHandler(TrackPacket packet) {
        TrackData.getInstance().setxPath(packet.getXPath());
        TrackData.getInstance().setyPath(packet.getYPath());
        TrackData.getInstance().setRotationPath(packet.getRotationPath());
        TrackData.getInstance().setPartPath(packet.getPartPath());
        Log.d("GameLobby", "Track Received");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("Track Received");
            }
        });
    }

    @Override
    public void newConnectionHandler(Connection connection) {
        if(mClient.getNumberOfConnections() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("Connected");
                }
            });
        }
    }

    @Override
    public void clientPacketHandler(ClientPacket packet) {
        if(lastTimeSync > 0) {
            long timePassed = System.currentTimeMillis() - lastTimeSync;
            clock.synchronizeTime(timePassed / 2);
        } else {
            clock.synchronizeTime();
            mClient.sendAll(packet);
        }
    }

    private class ConnectOperation extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            int count = params.length;
            for(int i = 0; i < count; i++) {
                Log.d("GameLobby", "Connecting to " + params[i]);
                mClient.connect(params[i]);
            }
            return mClient.getNumberOfConnections() > 0;
        }
    }

    private class SendTrackOperation extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            TrackPacket packet = new TrackPacket();
            packet.setXPath(TrackData.getInstance().getxPath());
            packet.setYPath(TrackData.getInstance().getyPath());
            packet.setPartPath(TrackData.getInstance().getPartPath());
            packet.setRotationPath(TrackData.getInstance().getRotationPath());
            Log.d("GameLobby", "Sending Track");
            mClient.sendAll(packet);
            ClientPacket clientPacket = new ClientPacket(TIME);
            lastTimeSync = System.currentTimeMillis();
            mClient.sendAll(clientPacket);
            return true;
        }
    }

}