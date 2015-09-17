package edu.virginia.cs.uvathingstodo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.util.Date;


public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener{

    protected GoogleApiClient mGoogleApiClient;

    protected Location mLastLocation;

    protected String mLatitudeText;
    protected String mLongitudeText;


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


    }

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

    public void itemPicked(View view) {
        switch(view.getId()) {

            case R.id.choice_1:
                TextView textView1 = (TextView) findViewById(R.id.display);
                Button button1 = (Button) findViewById(R.id.choice_1);
                String selection1 = button1.getText().toString();
                textView1.setText("You chose: " + selection1);
            break;

            case R.id.choice_2:
                TextView textView2 = (TextView) findViewById(R.id.display);
                Button button2 = (Button) findViewById(R.id.choice_2);
                String selection2 = button2.getText().toString();
                textView2.setText("You chose: " + selection2);
            break;

            case R.id.choice_3:
                TextView textView3 = (TextView) findViewById(R.id.display);
                Button button3 = (Button) findViewById(R.id.choice_3);
                String selection3 = button3.getText().toString();
                textView3.setText("You chose: " + selection3);
            break;
        }
    }


    public void getLocation(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_name);
        Context context = getApplicationContext();
        String name = editText.getText().toString();
        int duration = Toast.LENGTH_SHORT;

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText = (String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText = (String.valueOf(mLastLocation.getLongitude()));
        }

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (l != null) {
                Toast.makeText(this, "Location is " + l.getLatitude() + " " + l.getLongitude(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Location is null", Toast.LENGTH_LONG).show();
            }
        }
        catch (SecurityException se) {

        }

    }

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
}
