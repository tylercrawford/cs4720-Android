package edu.virginia.cs.uvathingstodo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class ItemActivity extends AppCompatActivity {

    String item_number;
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent intent = getIntent();
        name = intent.getStringExtra("item_name");
        item_number = intent.getStringExtra("item_number");
        String description = intent.getStringExtra("item_description");
        TextView textView1 = (TextView) findViewById(R.id.display);
        textView1.setText(name);
        TextView textView2 = (TextView) findViewById(R.id.description);
        textView2.setText(description);

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

    public void completedItem(View view) {

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, "Good Work! You completed " + name, duration);
        toast.show();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("item_completed", item_number);
        setResult(RESULT_OK, intent);
        finish();

        //startActivity(intent);

    }
}
