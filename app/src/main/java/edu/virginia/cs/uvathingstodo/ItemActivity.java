package edu.virginia.cs.uvathingstodo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class ItemActivity extends AppCompatActivity {

    String item_number;
    String name = "";

    String username;

    private DBHelper mydb;
    TextView title_view;
    TextView description_view;
    TextView completed_view;
    TextView location_view;
    TextView date_view;
    int id_To_Update = 0;
    Button camera_button;
    Button share_button;
    ImageView image;
    TextView shared_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        title_view = (TextView) findViewById(R.id.title_field);
        description_view = (TextView) findViewById(R.id.description_field);
        completed_view = (TextView) findViewById(R.id.completed_field);
        location_view = (TextView) findViewById(R.id.location_field);
        date_view = (TextView) findViewById(R.id.date_field);

        camera_button = (Button) findViewById(R.id.camera_button);
        image = (ImageView) findViewById(R.id.imageView);
        share_button = (Button) findViewById(R.id.share_button);
        shared_text = (TextView) findViewById(R.id.shared_text);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
//        int Value = extras.getInt("id");
//        Toast.makeText(getApplicationContext(), "Index value: "+Value, Toast.LENGTH_SHORT).show();
        if(extras != null) {
            int Value = extras.getInt("id");
            username = extras.getString("username");

            if(Value > 0) {
                Cursor rs = mydb.getData(Value, username);
                //Toast.makeText(getApplicationContext(), rs.toString(), Toast.LENGTH_SHORT).show();
                id_To_Update = Value;
                rs.moveToFirst();

//
                String title = rs.getString(rs.getColumnIndex(DBHelper.ITEMS_COULMN_TITLE));
                String description = rs.getString(rs.getColumnIndex(DBHelper.ITEMS_COLUMN_DESCRIPTION));
                String completed = rs.getString(rs.getColumnIndex(DBHelper.ITEMS_COLUMN_COMPLETED));
                double longitude = rs.getDouble(rs.getColumnIndex(DBHelper.ITEMS_COLUMN_LONGITUDE));
                double latitude = rs.getDouble(rs.getColumnIndex(DBHelper.ITEMS_COLUMN_LATITUDE));
                String date = rs.getString(rs.getColumnIndex(DBHelper.ITEMS_COLUMN_DATE));
                byte[] image_array = rs.getBlob(rs.getColumnIndex(DBHelper.ITEMS_COLUMN_IMAGE));
                int image_exists = rs.getInt(rs.getColumnIndex(DBHelper.ITEMS_COLUMN_IMAGE_EXISTS));
                int tweet_posted = rs.getInt(rs.getColumnIndex(DBHelper.ITEMS_COLUMN_TWEET_POSTED));

                if (!rs.isClosed()) {
                    rs.close();
                }

                if (completed.equals("Completed!")) {
                    Button b = (Button) findViewById(R.id.completed_button);
                    b.setVisibility(View.GONE);

                    location_view.setText("Longitude: " + longitude + " \nLatitude: " + latitude);

                    date = date.substring(0, date.length()-9);
                    date_view.setText("Completed: " + date);
                }
                else {
                    camera_button.setVisibility(View.GONE);
                    location_view.setVisibility(View.GONE);
                    date_view.setVisibility(View.GONE);
                    share_button.setVisibility(View.GONE);
                    shared_text.setVisibility(View.GONE);
                }

                if(tweet_posted == 1) {
                    share_button.setVisibility(View.GONE);
                    shared_text.setVisibility(View.VISIBLE);
                } else {
                    shared_text.setVisibility(View.GONE);
                }

                title_view.setText(title);
                description_view.setText(description);
                completed_view.setText(completed);

                if (image_exists == 1) {
                    Bitmap bp = BitmapFactory.decodeByteArray(image_array, 0, image_array.length);
                    image.setImageBitmap(bp);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.profile) {
            Bundle dataBundle = new Bundle();
            dataBundle.putString("username", username);

            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);

            intent.putExtras(dataBundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != 0) {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(bp);
            mydb.insertImage(id_To_Update, username, bp);
        }
    }

    public void completedItem(View view) {
        Bundle extras = getIntent().getExtras();

        double longitude = 0;
        double latitude = 0;

        String date;

        if(extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0) {
                LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                try {
                    Location l = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (l != null) {
                        longitude = l.getLongitude();
                        latitude = l.getLatitude();
                    } else {
                       // Toast.makeText(this, "Location is null", Toast.LENGTH_LONG).show();
                    }
                }
                catch (SecurityException se) {

                }

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss ");

                date = sdf.format(c.getTime());

                if(mydb.updateItem(id_To_Update, title_view.getText().toString(), description_view.getText().toString(), "Completed!", latitude, longitude, date, "", username, 0)) {
                    Button b = (Button) findViewById(R.id.completed_button);
                    b.setVisibility(View.GONE);

                    if (longitude != 0 && latitude != 0) {
                        location_view.setText("Longitude: " + longitude + " \nLatitude: " + latitude);
                    } else {
                        location_view.setText("Longitude: Not Available \nLatitude: Not Available");
                    }

                    date = date.substring(0, date.length()-9);
                    date_view.setText("Completed: " + date);
                    completed_view.setText("Completed!");

                    camera_button.setVisibility(View.VISIBLE);
                    location_view.setVisibility(View.VISIBLE);
                    date_view.setVisibility(View.VISIBLE);
                    share_button.setVisibility(View.VISIBLE);

                }
                else {
                    Toast.makeText(getApplicationContext(), "Failed Updating", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void tweetItem(View view) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("CZfZB6G9QvhvnzOYJCnSfjprD")
                .setOAuthConsumerSecret("wOVwYFIG2zUhbg8iHaVcFbqzJVosCECTDwaFhbbGR41Y1UDWrN")
                .setOAuthAccessToken("4259854637-ksfcHiieTwnTRqKkKITjnArH911KG7ZitT40uH5")
                .setOAuthAccessTokenSecret("Xy0EC4TMppB2In8PP3DHLMdjcfowXwimAkF43xztM5Zib");
        TwitterFactory tf = new TwitterFactory(cb.build());
        final Twitter twitter = tf.getInstance();

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

        final int finalCount = taskCount;

        new Thread() {
            @Override
            public void run() {
                try {
                    String tweet = username + " completed: " + title_view.getText();
                    tweet += "\nProgress: " + finalCount + "/100";
                    twitter.updateStatus(tweet);
                } catch (twitter4j.TwitterException te) {
                    System.out.println("Twitter Exception");
                }
            }
        }.start();

        if (haveNetworkConnection()) {
            mydb.postTweet(id_To_Update, username);
            share_button.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(getApplicationContext(), "Unable to post tweet: no internet access", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean haveNetworkConnection() {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
