package edu.virginia.cs.uvathingstodo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
