package edu.virginia.cs.uvathingstodo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Created by ScottThinkPad on 9/28/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TestDatabase3.db";
    public static final String ITEMS_TABLE_NAME = "items";
    public static final String ITEMS_COLUMN_ID = "id";
    public static final String ITEMS_COULMN_TITLE = "title";
    public static final String ITEMS_COLUMN_DESCRIPTION = "description";
    public static final String ITEMS_COLUMN_COMPLETED = "completed";
    public static final String ITEMS_COLUMN_LATITUDE = "latitude";
    public static final String ITEMS_COLUMN_LONGITUDE = "longitude";
    public static final String ITEMS_COLUMN_DATE = "date";
    public static final String ITEMS_COLUMN_IMAGE = "image";

    private ArrayList<String[]> itemList = new ArrayList<String[]>();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(
                "create table items " +
                        "(id integer primary key, title text, description text, completed text, latitude real, longitude real, date text, image blob)"
        );
        db.execSQL(
                "create table users " +
                        "(id integer primary key, username text, password text)"
        );
//        CreateArrayList();
//        for (String[] item : itemList) {
//            insertItem(item[0], item[1]);
//        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }

    public void DropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }

    public boolean registerUser (String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        db.insert("users", null, contentValues);
        String table_name = username+"_items";
//        db.execSQL(
//                "create table " + table_name + " " +
//                        "(id integer primary key, title text, description text, completed text, latitude real, longitude real, date text, image blob)"
//        );
        return true;
    }

    public boolean insertItem (String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEMS_COULMN_TITLE, title);
        contentValues.put(ITEMS_COLUMN_DESCRIPTION, description);
        contentValues.put(ITEMS_COLUMN_COMPLETED, "Not yet completed");
        contentValues.put(ITEMS_COLUMN_LATITUDE, 0);
        contentValues.put(ITEMS_COLUMN_LONGITUDE, 0);
        contentValues.put(ITEMS_COLUMN_DATE, "");
        contentValues.put(ITEMS_COLUMN_IMAGE, "");
        db.insert(ITEMS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getPassword(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT password FROM users WHERE username='"+username+"'", null);
        return res;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM items WHERE id='"+id+"'", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ITEMS_TABLE_NAME);
        return  numRows;
    }

    public boolean updateItem (Integer id, String title, String description, String completed, double lat, double lon, String date, String img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEMS_COULMN_TITLE, title);
        contentValues.put(ITEMS_COLUMN_DESCRIPTION, description);
        contentValues.put(ITEMS_COLUMN_COMPLETED, completed);
        contentValues.put(ITEMS_COLUMN_LATITUDE, lat);
        contentValues.put(ITEMS_COLUMN_LONGITUDE, lon);
        contentValues.put(ITEMS_COLUMN_DATE, date);
        contentValues.put(ITEMS_COLUMN_IMAGE, img);
        db.update(ITEMS_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id)} );
        return  true;
    }

    public Integer deleteItem (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ITEMS_TABLE_NAME, "id = ? ", new String[] { Integer.toString(id)});
    }

    public ArrayList<String> getAllItems() {
        ArrayList<String> items = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from items", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            items.add(res.getString(res.getColumnIndex(ITEMS_COULMN_TITLE)));
            res.moveToNext();
        }
        return items;
    }

    private void CreateArrayList() {
        itemList.add(new String[] {"Nab the #1 Ticket at Bodo's", "Get up early and snag the first Bodo's ticket.  Thousands of customers come to this corner shop every day.  Can you beat the crowd?"});
        itemList.add(new String[] {"Dining Hall Marathon", "Go eat at all three dining halls in one day!"});
        itemList.add(new String[] {"See a Horse at Foxfield", "Going to Foxfield can be a blast, but make sure you try to remember to see a horse! It is a race after all"});
        itemList.add(new String[] {"Paint Beta Bridge", "Beta Bridge has een painted over so many times that it has almost half a foot of paint built up on its walls.  Leave your mark on this landmark!"});
        itemList.add(new String[] {"Take a Professor to Lunch", "You sit in their class.  You may visit their Office Hours.  But to really have the UVA experience Jefferson wanted, get to know them with a free lunch (yes, ODOS gives you the money)."});
        itemList.add(new String[] {"Play in Mad Bowl", "Don't let that beautiful pit go to waste.  Grab some friends and a ball of some sort and play a game to enjoy a nice day, or wait til it snows and bring a sled."});
        itemList.add(new String[] {"Go Streaking", "A tradition for the past 40 years, the academical village serves a perfect place for a late night run."});
        itemList.add(new String[] {"Eat at Bellair Market", "Just a little trip down Ivy, this small gas station has amazing sandwiches.  Recommendation: the Ednam"});
        itemList.add(new String[] {"Go to Rotunda Sing", "UVA has tons of talented A Capella groups.  Hear them all debut on the steps of the Rotunda to see which is your favorite."});
        itemList.add(new String[] {"Eat a Gus Burger", "Though I don't recommend it for the taste, getting a Gus Burger from the White Spot is a tradition very old and true at the University."});
        itemList.add(new String[] {"Swim at Blue Hole", "Go take a drive to the depths of nature and take a two mile hike to this wonderful waterfall swimming area!"});
        itemList.add(new String[] {"Chow Down at Crozet Pizza", "And by this, we mean the ORIGINAL Crozet Pizza.  Though the one on the Corner is still delicious and has great drink options, the original is worth the trek."});
        itemList.add(new String[] {"Attend Restoration Ball", "This annual event hosted by the Jefferson Literary and Debating Society brings people from all over the University together for a formal ball, and Dean Groves may teach the Virginia Reel."});
        itemList.add(new String[] {"Go to a Frat Party", "You may think these are for the younger generations, but relive your first year weekend nights by taking a stroll down Rugby Road and walk through an overcrowded house that reeks of kegs and old pizza."});
        itemList.add(new String[] {"Make a Purchase at Shady Grady", "Remember that place down on Preston Ave that was the only place that didn't care about IDs? It is still there and still just as shady."});
        itemList.add(new String[] {"Nab the #1 Ticket at Bodo's", "Get up early and snag the first Bodo's ticket.  Thousands of customers come to this corner shop every day.  Can you beat the crowd?"});
        itemList.add(new String[] {"Dining Hall Marathon", "Go eat at all three dining halls in one day!"});
        itemList.add(new String[] {"See a Horse at Foxfield", "Going to Foxfield can be a blast, but make sure you try to remember to see a horse! It is a race after all"});
        itemList.add(new String[] {"Paint Beta Bridge", "Beta Bridge has een painted over so many times that it has almost half a foot of paint built up on its walls.  Leave your mark on this landmark!"});
        itemList.add(new String[] {"Take a Professor to Lunch", "You sit in their class.  You may visit their Office Hours.  But to really have the UVA experience Jefferson wanted, get to know them with a free lunch (yes, ODOS gives you the money)."});
        itemList.add(new String[] {"Play in Mad Bowl", "Don't let that beautiful pit go to waste.  Grab some friends and a ball of some sort and play a game to enjoy a nice day, or wait til it snows and bring a sled."});
        itemList.add(new String[] {"Go Streaking", "A tradition for the past 40 years, the academical village serves a perfect place for a late night run."});
        itemList.add(new String[] {"Eat at Bellair Market", "Just a little trip down Ivy, this small gas station has amazing sandwiches.  Recommendation: the Ednam"});
        itemList.add(new String[] {"Go to Rotunda Sing", "UVA has tons of talented A Capella groups.  Hear them all debut on the steps of the Rotunda to see which is your favorite."});
        itemList.add(new String[] {"Eat a Gus Burger", "Though I don't recommend it for the taste, getting a Gus Burger from the White Spot is a tradition very old and true at the University."});
        itemList.add(new String[] {"Swim at Blue Hole", "Go take a drive to the depths of nature and take a two mile hike to this wonderful waterfall swimming area!"});
        itemList.add(new String[] {"Chow Down at Crozet Pizza", "And by this, we mean the ORIGINAL Crozet Pizza.  Though the one on the Corner is still delicious and has great drink options, the original is worth the trek."});
        itemList.add(new String[] {"Attend Restoration Ball", "This annual event hosted by the Jefferson Literary and Debating Society brings people from all over the University together for a formal ball, and Dean Groves may teach the Virginia Reel."});
        itemList.add(new String[] {"Go to a Frat Party", "You may think these are for the younger generations, but relive your first year weekend nights by taking a stroll down Rugby Road and walk through an overcrowded house that reeks of kegs and old pizza."});
        itemList.add(new String[] {"Make a Purchase at Shady Grady", "Remember that place down on Preston Ave that was the only place that didn't care about IDs? It is still there and still just as shady."});

    }



}
