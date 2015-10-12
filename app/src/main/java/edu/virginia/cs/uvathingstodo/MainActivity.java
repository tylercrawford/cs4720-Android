package edu.virginia.cs.uvathingstodo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;


//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
//import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.util.ArrayList;
import java.util.Random;

//public class MainActivity extends AppCompatActivity implements
//        ConnectionCallbacks, OnConnectionFailedListener{
public class MainActivity extends AppCompatActivity {

//    protected GoogleApiClient mGoogleApiClient;

//    protected Location mLastLocation;
//    protected String mLatitudeText;
//    protected String mLongitudeText;

    ArrayList<String> itemList = new ArrayList<String>();
    ArrayList<String[]> itemList2 = new ArrayList<String[]>();
    ArrayAdapter<String> adapter;

    int ItemActivityRequestCode;
    private ListView listView;
    DBHelper mydb;
    String username;

    LocationManager mlocManager;
    LocationListener mlocListener;


//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ItemActivityRequestCode && resultCode == RESULT_OK && data != null) {
            String number = data.getStringExtra("item_completed");
//            Context context = getApplicationContext();
//            int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(context, name, duration);
//            toast.show();
            int position = Integer.parseInt(number);
            String oldText = itemList.get(position);
            itemList.set(position, oldText + " (Completed)");
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_horizontal);
        }
        else {
            setContentView(R.layout.activity_main);
        }

        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();

        try {
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        }
        catch (SecurityException se) {

        }

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            username = extras.getString("username");
        }

        mydb = new DBHelper(this);
        if (mydb.numberOfRows(username) == 0) {
            addItems();
            for (String[] item : itemList2) {
                mydb.insertItem(item[0], item[1], username);
            }
        }

        ArrayList array_list = mydb.getAllItems(username);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);

        TextView welcomeText = (TextView)findViewById(R.id.welcome);
        welcomeText.setText("Hello, " + username);

        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int id_To_Search = position + 1;

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);
                dataBundle.putString("username", username);

                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList array_list = mydb.getAllItems(username);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int id_To_Search = position + 1;

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);
                dataBundle.putString("username", username);

                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });

        // Handle Profile Section
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //TextView text = (TextView) findViewById(R.id.profile_name);
            //text.setText("Hello "+ username);
            TextView prof_username = (TextView) findViewById(R.id.profile_username);
            TextView numCompleted = (TextView) findViewById(R.id.profile_numTasks);
            prof_username.setText(prof_username.getText() + username);

            Cursor rs;
            int taskCount = 0;
            int numRows = mydb.numberOfRows(username);
            for (int i = 1; i <= numRows; i++) {
                rs = mydb.getData(i, username);
                rs.moveToFirst();
                String completed = rs.getString(rs.getColumnIndex(DBHelper.ITEMS_COLUMN_COMPLETED));
                if (completed.equals("Completed!")) {
                    taskCount++;
                }

                if (!rs.isClosed()) {
                    rs.close();
                }
            }

            numCompleted.setText(numCompleted.getText() + "" + taskCount);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();

//        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    protected  void onDestroy() {
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            mlocManager.removeUpdates(mlocListener);
        }
        catch (SecurityException se) {

        }
        super.onDestroy();
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

        if (id == R.id.logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void randomItem(View view) {
        Random rand = new Random();
        int position = rand.nextInt(mydb.numberOfRows(username));

        Bundle dataBundle = new Bundle();
        dataBundle.putInt("id", position);
        dataBundle.putString("username", username);

        Intent intent = new Intent(getApplicationContext(), ItemActivity.class);

        intent.putExtras(dataBundle);
        startActivity(intent);
    }

    public void addItems() {
        // This will be replaced by reading from local storage by the next milestone
        itemList2.add(new String[] {"Nab the #1 Ticket at Bodo's!", "Get up early and snag the first Bodo's ticket.  Thousands of customers come to this corner shop every day.  Can you beat the crowd?"});
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

    public class MyLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location loc)
        {

        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(),
                    "Gps Disabled",
                    Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Toast.makeText(getApplicationContext(),
                    "Gps Enabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }
    }/* End of Class MyLocationListener */
}
