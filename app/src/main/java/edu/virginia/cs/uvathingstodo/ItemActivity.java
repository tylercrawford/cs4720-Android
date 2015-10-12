package edu.virginia.cs.uvathingstodo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
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


public class ItemActivity extends AppCompatActivity {

    String username;

    private DBHelper mydb;
    TextView title_view;
    TextView description_view;
    TextView completed_view;
    TextView location_view;
    TextView date_view;
    int id_To_Update = 0;
    Button camera_button;
    ImageView image;


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

                if (!rs.isClosed()) {
                    rs.close();
                }

                if (completed.equals("Completed!")) {
                    Button b = (Button) findViewById(R.id.completed_button);
                    b.setVisibility(View.GONE);

                    location_view.setText("Longitude: " + longitude + " \nLatitude: " + latitude);
                    date_view.setText("Completed: " + date);
                }
                else {
                    camera_button.setVisibility(View.GONE);
                    location_view.setVisibility(View.GONE);
                    date_view.setVisibility(View.GONE);

                }

                title_view.setText(title);
                description_view.setText(description);
                completed_view.setText(completed);

                if (image_exists == 1) {
                    //Toast.makeText(getApplicationContext(), image_array.toString(), Toast.LENGTH_SHORT).show();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bp = (Bitmap) data.getExtras().get("data");
        image.setImageBitmap(bp);
        mydb.insertImage(id_To_Update, username, bp);
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

                if(mydb.updateItem(id_To_Update, title_view.getText().toString(), description_view.getText().toString(), "Completed!", longitude, latitude, date, "", username)) {
                    Button b = (Button) findViewById(R.id.completed_button);
                    b.setVisibility(View.GONE);

                    location_view.setText("Longitude: " + longitude + " \nLatitude: " + latitude);
                    date_view.setText("Completed: " + date);
                    completed_view.setText("Completed!");

                    camera_button.setVisibility(View.VISIBLE);
                    location_view.setVisibility(View.VISIBLE);
                    date_view.setVisibility(View.VISIBLE);


                }
                else {
                    Toast.makeText(getApplicationContext(), "Failed Updating", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
