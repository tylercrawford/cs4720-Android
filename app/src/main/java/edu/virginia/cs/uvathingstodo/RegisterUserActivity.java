package edu.virginia.cs.uvathingstodo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterUserActivity extends AppCompatActivity {

    TextView username;
    TextView password;
    TextView password2;
    Button register_button;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        username = (TextView) findViewById(R.id.register_username);
        password = (TextView) findViewById(R.id.register_password);
        password2 = (TextView) findViewById(R.id.register_password2);
        register_button = (Button) findViewById(R.id.register_submit);
        mydb = new DBHelper(this);

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
                    Toast.makeText(getApplicationContext(), "Sorry, but '" + username_field + "' is already taken", Toast.LENGTH_SHORT).show();
                } else if (username_field.equals("")) {
                    username.setText("");
                    username.setHint("Username");
                    Toast.makeText(getApplicationContext(), "Username cannot be blank", Toast.LENGTH_SHORT).show();
                } else if (username_field.contains(" ")) {
                    username.setText("");
                    username.setHint("Username");
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
                    mydb.registerUser(username_field, password_field);
                    Toast.makeText(getApplicationContext(), "User registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
