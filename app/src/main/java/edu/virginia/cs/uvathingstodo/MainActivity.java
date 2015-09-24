package edu.virginia.cs.uvathingstodo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener{

    protected GoogleApiClient mGoogleApiClient;

    protected Location mLastLocation;

    protected String mLatitudeText;
    protected String mLongitudeText;

    ArrayList<String> itemList = new ArrayList<String>();
    ArrayList<String[]> itemList2 = new ArrayList<String[]>();
    ArrayAdapter<String> adapter;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_main);
        mGoogleApiClient.connect();

        addItems();
//
        ListView listView = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList);


        listView.setOnItemClickListener(mMessageClickedHandler);
        listView.setAdapter(adapter);

    }

    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            //Toast.makeText(getApplicationContext(), itemList.get(position), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
            intent.putExtra("item_name", itemList.get(position));
            intent.putExtra("item_description", itemList2.get(position)[1]);

            startActivity(intent);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void nameSubmit(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_name);
        Context context = getApplicationContext();
        String name = editText.getText().toString();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, "Hello, " + name + "!", duration);
        toast.show();
    }

    public void randomItem(View view) {
        Random rand = new Random();
        int position = rand.nextInt(itemList.size());

        Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
        intent.putExtra("item_name", itemList.get(position));
        intent.putExtra("item_description", itemList2.get(position)[1]);

        startActivity(intent);

    }

//    public void itemPicked(View view) {
//        switch(view.getId()) {
//
//            case R.id.choice_1:
//                TextView textView1 = (TextView) findViewById(R.id.display);
//                Button button1 = (Button) findViewById(R.id.choice_1);
//                String selection1 = button1.getText().toString();
//                textView1.setText("You chose: " + selection1);
//                Intent intent = new Intent(this, ItemActivity.class);
//                startActivity(intent);
//            break;
//
//            case R.id.choice_2:
//                TextView textView2 = (TextView) findViewById(R.id.display);
//                Button button2 = (Button) findViewById(R.id.choice_2);
//                String selection2 = button2.getText().toString();
//                textView2.setText("You chose: " + selection2);
//            break;
//
//            case R.id.choice_3:
//                TextView textView3 = (TextView) findViewById(R.id.display);
//                Button button3 = (Button) findViewById(R.id.choice_3);
//                String selection3 = button3.getText().toString();
//                textView3.setText("You chose: " + selection3);
//            break;
//        }
//    }


//    public void getLocation(View view) {
//        EditText editText = (EditText) findViewById(R.id.edit_name);
//        Context context = getApplicationContext();
//        String name = editText.getText().toString();
//        int duration = Toast.LENGTH_SHORT;
//
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            mLatitudeText = (String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText = (String.valueOf(mLastLocation.getLongitude()));
//        }
//
//        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//        try {
//            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (l != null) {
//                Toast.makeText(this, "Location is " + l.getLatitude() + " " + l.getLongitude(), Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Location is null", Toast.LENGTH_LONG).show();
//            }
//        }
//        catch (SecurityException se) {
//
//        }
//
//    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText = (String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText = (String.valueOf(mLastLocation.getLongitude()));
        } else {
            //Toast.makeText(this, "No Location", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        //Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        //Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public void addItems() {
        // This will be replaced by reading from local storage by the next milestone
        itemList2.add(new String[] {"Nab the #1 Ticket at Bodo's", "Get up early and snag the first Bodo's ticket.  Thousands of customers come to this corner shop every day.  Can you beat the crowd?"});
        itemList2.add(new String[] {"Dining Hall Marathon", "Go eat at all three dining halls in one day!"});
        itemList2.add(new String[] {"See a Horse at Foxfield", "Going to Foxfield can be a blast, but make sure you try to remember to see a horse! It is a race after all"});
        itemList2.add(new String[] {"Paint Beta Bridge", "Beta Bridge has een painted over so many times that it has almost half a foot of paint built up on its walls.  Leave your mark on this landmark!"});
        itemList2.add(new String[] {"Take a Professor to Lunch", "You sit in their class.  You may visit their Office Hours.  But to really have the UVA experience Jefferson wanted, get to know them with a free lunch (yes, ODOS gives you the money)."});
        itemList2.add(new String[] {"Play in Mad Bowl", "Don't let that beautiful pit go to waste.  Grab some friends and a ball of some sort and play a game to enjoy a nice day, or wait til it snows and bring a sled."});
        itemList2.add(new String[] {"Go Streaking", "A tradition for the past 40 years, the academical village serves a perfect place for a late night run."});
        itemList2.add(new String[] {"Eat at Bellair Market", "Just a little trip down Ivy, this small gas station has amazing sandwiches.  Recommendation: the Ednam"});
        itemList2.add(new String[] {"Go to Rotunda Sing", "UVA has tons of talented A Capella groups.  Hear them all debut on the steps of the Rotunda to see which is your favorite."});
        itemList2.add(new String[] {"Eat a Gus Burger", "Though I don't recommend it for the taste, getting a Gus Burger from the White Spot is a tradition very old and true at the University."});
        itemList2.add(new String[] {"Swim at Blue Hole", "Go take a drive to the depths of nature and take a two mile hike to this wonderful waterfall swimming area!"});
        itemList2.add(new String[] {"Chow Down at Crozet Pizza", "And by this, we mean the ORIGINAL Crozet Pizza.  Though the one on the Corner is still delicious and has great drink options, the original is worth the trek."});
        itemList2.add(new String[] {"Attend Restoration Ball", "This annual event hosted by the Jefferson Literary and Debating Society brings people from all over the University together for a formal ball, and Dean Groves may teach the Virginia Reel."});
        itemList2.add(new String[] {"Go to a Frat Party", "You may think these are for the younger generations, but relive your first year weekend nights by taking a stroll down Rugby Road and walk through an overcrowded house that reeks of kegs and old pizza."});
        itemList2.add(new String[] {"Make a Purchase at Shady Grady", "Remember that place down on Preston Ave that was the only place that didn't care about IDs? It is still there and still just as shady."});
        itemList2.add(new String[] {"Nab the #1 Ticket at Bodo's", "Get up early and snag the first Bodo's ticket.  Thousands of customers come to this corner shop every day.  Can you beat the crowd?"});
        itemList2.add(new String[] {"Dining Hall Marathon", "Go eat at all three dining halls in one day!"});
        itemList2.add(new String[] {"See a Horse at Foxfield", "Going to Foxfield can be a blast, but make sure you try to remember to see a horse! It is a race after all"});
        itemList2.add(new String[] {"Paint Beta Bridge", "Beta Bridge has een painted over so many times that it has almost half a foot of paint built up on its walls.  Leave your mark on this landmark!"});
        itemList2.add(new String[] {"Take a Professor to Lunch", "You sit in their class.  You may visit their Office Hours.  But to really have the UVA experience Jefferson wanted, get to know them with a free lunch (yes, ODOS gives you the money)."});
        itemList2.add(new String[] {"Play in Mad Bowl", "Don't let that beautiful pit go to waste.  Grab some friends and a ball of some sort and play a game to enjoy a nice day, or wait til it snows and bring a sled."});
        itemList2.add(new String[] {"Go Streaking", "A tradition for the past 40 years, the academical village serves a perfect place for a late night run."});
        itemList2.add(new String[] {"Eat at Bellair Market", "Just a little trip down Ivy, this small gas station has amazing sandwiches.  Recommendation: the Ednam"});
        itemList2.add(new String[] {"Go to Rotunda Sing", "UVA has tons of talented A Capella groups.  Hear them all debut on the steps of the Rotunda to see which is your favorite."});
        itemList2.add(new String[] {"Eat a Gus Burger", "Though I don't recommend it for the taste, getting a Gus Burger from the White Spot is a tradition very old and true at the University."});
        itemList2.add(new String[] {"Swim at Blue Hole", "Go take a drive to the depths of nature and take a two mile hike to this wonderful waterfall swimming area!"});
        itemList2.add(new String[] {"Chow Down at Crozet Pizza", "And by this, we mean the ORIGINAL Crozet Pizza.  Though the one on the Corner is still delicious and has great drink options, the original is worth the trek."});
        itemList2.add(new String[] {"Attend Restoration Ball", "This annual event hosted by the Jefferson Literary and Debating Society brings people from all over the University together for a formal ball, and Dean Groves may teach the Virginia Reel."});
        itemList2.add(new String[] {"Go to a Frat Party", "You may think these are for the younger generations, but relive your first year weekend nights by taking a stroll down Rugby Road and walk through an overcrowded house that reeks of kegs and old pizza."});
        itemList2.add(new String[] {"Make a Purchase at Shady Grady", "Remember that place down on Preston Ave that was the only place that didn't care about IDs? It is still there and still just as shady."});

        for (int i = 0; i < itemList2.size(); i++) {
            itemList.add(itemList2.get(i)[0]);
        }
    }
}
