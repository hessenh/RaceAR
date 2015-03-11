package com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qualcomm.vuforia.samples.VuforiaSamples.R;

public class Tutorial extends Activity implements Button.OnClickListener {

    private Button mNextBtn;
    private ImageView mTutorialImg;
    private int counter;
    private TextView text;
    private Button mPrevBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        mTutorialImg = (ImageView)findViewById(R.id.imageView);
        mTutorialImg.setImageResource(R.drawable.menueraw);

        counter = 0;

        text = (TextView)findViewById(R.id.textView);
        text.setText("In VirTrack you can either create your own track or join an exciting one!");

        mNextBtn = (Button)findViewById(R.id.nextBtn);
        mNextBtn.setOnClickListener(this);

        mPrevBtn = (Button)findViewById(R.id.prevBtn);
        mPrevBtn.setOnClickListener(this);


    }
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.nextBtn:
                counter++;
                break;
            case R.id.prevBtn:
                counter--;
                break;
        }
        System.out.println(counter);

        switch (counter) {
            case 0:
                mTutorialImg.setImageResource(R.drawable.menueraw);
                text.setText("In VirTrack you can either create your own track or join an exciting one!");
                break;
            case 1:
                mTutorialImg.setImageResource(R.drawable.markerraw);
                text.setText("The blue marker shows where the part will attach.\n " +
                        "Hold your finger on the screen to get the a new part!");
                break;
            case 2:
                mTutorialImg.setImageResource(R.drawable.part);
                text.setText("This is a straight part! You can move your phone to place it where you like!");
                break;
            case 3:
                mTutorialImg.setImageResource(R.drawable.rotateraw);
                text.setText("Tap once to rotate the part 90 degrees");
                break;
            case 4:
                mTutorialImg.setImageResource(R.drawable.placepartraw);
                text.setText("Hold your finger on the screen to place the part");
                break;
            case 5:
                mTutorialImg.setImageResource(R.drawable.placepartraw);
                text.setText("Swipe your finger from left to right to get to the menu");
                break;
            case 6:
                mTutorialImg.setImageResource(R.drawable.finished);
                text.setText("When the track is finished, a blue box will try it out!");
                break;
            case 7:
                mTutorialImg.setImageResource(R.drawable.duellblur);
                text.setText("Now it's time to test out the track! \n " +
                        "There are two ways of doing that: \n" +
                        "\n" +
                        "- Create a game and play \n" +
                        "- Go directly to lobby");
                break;
            case 8:
                mTutorialImg.setImageResource(R.drawable.duellblur);
                text.setText("If you're the creator of the track, insert your opponents IP and connect, send track and play! \n" +
                        "\n" +
                        "If not, wait for the connection and track, then play!");
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
