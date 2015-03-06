package com.qualcomm.vuforia.samples.VuforiaSamples.app.ImageTargets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qualcomm.vuforia.samples.VuforiaSamples.R;

public class ShowPartGallery extends Activity {

    private ListView mDrawerList;
    private final String[] parts_content = {"Back","Straight","Turn","Marker","Tunnel"};
    private TextView parts_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_part_gallery);

        parts_title = (TextView)findViewById(R.id.settingsTitle);
        parts_title.setText("Parts");

        //Setting the sidebar
        mDrawerList = (ListView) findViewById(R.id.left_part_gallery);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, parts_content));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        System.out.println("Back from gallery");
                        Intent data = new Intent();
                        data.putExtra("part", parts_title.getText());
                        setResult(RESULT_OK, data);
                        finish();
                        break;
                    case 1:
                       parts_title.setText("Straight");
                        break;
                    case 2:
                        parts_title.setText("Turn");
                        break;
                    case 3:
                        parts_title.setText("Marker");
                        break;
                    case 4:
                        parts_title.setText("Tunnel");
                        break;
                    default:
                        System.out.println("DEFAULT from settings");
                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_part_gallery, menu);
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
