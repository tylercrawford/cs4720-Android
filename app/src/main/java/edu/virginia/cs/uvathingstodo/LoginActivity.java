package edu.virginia.cs.uvathingstodo;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button register_button,login_button;
    EditText username_field,pword_field;

    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mydb = new DBHelper(this);

        register_button=(Button)findViewById(R.id.registerbtn);
        username_field=(EditText)findViewById(R.id.username);
        pword_field=(EditText)findViewById(R.id.pword);

        login_button=(Button)findViewById(R.id.login);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = username_field.getText().toString();
                String password_entered = pword_field.getText().toString();
                Cursor rs = mydb.getPassword(username);
                rs.moveToFirst();

                try {
                    String password_registered = rs.getString(rs.getColumnIndex("password"));
                    //Toast.makeText(getApplicationContext(), "Password: " + password_registered, Toast.LENGTH_SHORT).show();


                    if (password_entered.equals(password_registered)) {
                        //Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
                        Bundle dataBundle = new Bundle();
                        dataBundle.putString("username", username);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        intent.putExtras(dataBundle);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                        pword_field.setText("");
                        pword_field.setHint("Password");

                    }
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Username does not exist", Toast.LENGTH_SHORT).show();
                    pword_field.setText("");
                    pword_field.setHint("Password");

                }

            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterUserActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
