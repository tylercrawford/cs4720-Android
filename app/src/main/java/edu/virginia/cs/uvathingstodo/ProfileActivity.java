package edu.virginia.cs.uvathingstodo;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    String username;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            username = extras.getString("username");
        }
        mydb = new DBHelper(this);

        TextView prof_username = (TextView) findViewById(R.id.profile_username_portrait);
        TextView numCompleted = (TextView) findViewById(R.id.profile_numTasks_portrait);
        TextView yearText = (TextView) findViewById(R.id.profile_year);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_portrait);
        ImageView image = (ImageView) findViewById(R.id.prof_img);

        prof_username.setText("Username: " + username);

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

        boolean imgExists = false;
        byte[] image_array = null;
        Cursor userCursor = mydb.getUserData(username);
        if (userCursor.moveToFirst()) {
            if (userCursor.getInt(userCursor.getColumnIndex("image_exists")) == 1) {
                imgExists = true;
                image_array = userCursor.getBlob(userCursor.getColumnIndex("image"));
            }
            yearText.setText(userCursor.getString(userCursor.getColumnIndex("year")));


        }

        if (imgExists) {
            Bitmap bp = BitmapFactory.decodeByteArray(image_array, 0, image_array.length);
            image.setImageBitmap(bp);
        }

        numCompleted.setText("Tasks completed: " + taskCount + "/100");
        progressBar.setProgress(taskCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. This action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.profile_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
