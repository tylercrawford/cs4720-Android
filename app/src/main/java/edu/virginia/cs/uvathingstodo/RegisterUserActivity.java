package edu.virginia.cs.uvathingstodo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterUserActivity extends AppCompatActivity {

    TextView username;
    TextView password;
    TextView password2;
    Button register_button;
    Button camera_button;
    DBHelper mydb;

    Bitmap image;
    ImageView imageview;

    String year = "4th year";
    boolean imageTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        username = (TextView) findViewById(R.id.register_username);
        password = (TextView) findViewById(R.id.register_password);
        password2 = (TextView) findViewById(R.id.register_password2);
        register_button = (Button) findViewById(R.id.register_submit);
        camera_button = (Button) findViewById(R.id.camera_button);
        imageview = (ImageView) findViewById(R.id.imageView);
        mydb = new DBHelper(this);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_field = username.getText().toString();
                String password_field = password.getText().toString();
                String password2_field = password2.getText().toString();

                Cursor cursor = mydb.getUserData(username_field);
                cursor.moveToFirst();

                boolean uniqueUser;
                try {
                    String username_registered = cursor.getString(cursor.getColumnIndex("username"));
                    uniqueUser = false;
                }
                catch (Exception e) {
                    uniqueUser = true;
                }

                if (!uniqueUser) {
                    username.setText("");
                    username.setHint("Username");
                    password.setText("");
                    password2.setText("");
                    password.setHint("Password");
                    password2.setHint("Retype Password");
                    Toast.makeText(getApplicationContext(), "Sorry, but '" + username_field + "' is already taken", Toast.LENGTH_SHORT).show();
                } else if (username_field.equals("")) {
                    username.setText("");
                    username.setHint("Username");
                    password.setText("");
                    password2.setText("");
                    password.setHint("Password");
                    password2.setHint("Retype Password");
                    Toast.makeText(getApplicationContext(), "Username cannot be blank", Toast.LENGTH_SHORT).show();
                } else if (username_field.contains(" ")) {
                    username.setText("");
                    username.setHint("Username");
                    password.setText("");
                    password2.setText("");
                    password.setHint("Password");
                    password2.setHint("Retype Password");
                    Toast.makeText(getApplicationContext(), "Username cannot contain spaces", Toast.LENGTH_SHORT).show();
                } else if (password_field.equals("")) {
                    password.setText("");
                    password2.setText("");
                    password.setHint("Password");
                    password2.setHint("Retype Password");
                    Toast.makeText(getApplicationContext(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
                } else if (!password_field.equals(password2_field)){
                    password.setText("");
                    password2.setText("");
                    password.setHint("Password");
                    password2.setHint("Retype Password");
                    Toast.makeText(getApplicationContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
                } else {
                    if (imageTaken) {
                        mydb.registerUserWithImg(username_field, password_field, year, image);
                    } else {
                        mydb.registerUserWithoutImg(username_field, password_field, year);
                    }

                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("username", username_field);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    intent.putExtras(dataBundle);
                    startActivity(intent);

                    Toast.makeText(getApplicationContext(), "User registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_1st:
                if (checked)
                    year = "1st year";
                break;
            case R.id.radio_2nd:
                if (checked)
                    year = "2nd year";
                break;
            case R.id.radio_3rd:
                if (checked)
                    year = "3rd year";
                break;
            case R.id.radio_4th:
                if (checked)
                    year = "4th year";
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        image = (Bitmap) data.getExtras().get("data");
        imageview.setImageBitmap(image);
        imageTaken = true;
    }
}
