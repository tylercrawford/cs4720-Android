package edu.virginia.cs.uvathingstodo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


/**
 * Created by ScottThinkPad on 9/28/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ProductionDatabase.db";
    public static final String ITEMS_TABLE_NAME = "items";
    public static final String ITEMS_COLUMN_ID = "id";
    public static final String ITEMS_COULMN_TITLE = "title";
    public static final String ITEMS_COLUMN_DESCRIPTION = "description";
    public static final String ITEMS_COLUMN_COMPLETED = "completed";
    public static final String ITEMS_COLUMN_LATITUDE = "latitude";
    public static final String ITEMS_COLUMN_LONGITUDE = "longitude";
    public static final String ITEMS_COLUMN_DATE = "date";
    public static final String ITEMS_COLUMN_IMAGE = "image";
    public static final String ITEMS_COLUMN_IMAGE_EXISTS = "image_exists";
    public static final String ITEMS_COLUMN_TWEET_POSTED = "tweet_posted";

    private ArrayList<String[]> itemList = new ArrayList<String[]>();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(
                "create table items " +
                        "(id integer primary key, title text, description text, completed text, latitude real, longitude real, date text, image blob, image_exists integer, tweet_posted integer)"
        );
        db.execSQL(
                "create table users " +
                        "(id integer primary key, username text, password text, image blob, image_exists integer, year text)"
        );
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

    public boolean registerUserWithImg (String username, String password, String year, Bitmap img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        contentValues.put("image", byteArray);
        contentValues.put("image_exists", 1);

        contentValues.put("year", year);
        contentValues.put("username", username);
        contentValues.put("password", password);
        db.insert("users", null, contentValues);
        String table_name = username+"_items";
        db.execSQL(
                "create table " + table_name + " " +
                        "(id integer primary key, title text, description text, completed text, latitude real, longitude real, date text, image blob, image_exists integer, tweet_posted integer)"
        );
        return true;
    }

    public boolean registerUserWithoutImg (String username, String password, String year) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("image_exists", 0);

        contentValues.put("year", year);
        contentValues.put("username", username);
        contentValues.put("password", password);
        db.insert("users", null, contentValues);
        String table_name = username+"_items";
        db.execSQL(
                "create table " + table_name + " " +
                        "(id integer primary key, title text, description text, completed text, latitude real, longitude real, date text, image blob, image_exists integer, tweet_posted integer)"
        );
        return true;
    }

    public boolean insertItem (String title, String description, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table_name = username + "_items";
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEMS_COULMN_TITLE, title);
        contentValues.put(ITEMS_COLUMN_DESCRIPTION, description);
        contentValues.put(ITEMS_COLUMN_COMPLETED, "Not yet completed");
        contentValues.put(ITEMS_COLUMN_LATITUDE, 0);
        contentValues.put(ITEMS_COLUMN_LONGITUDE, 0);
        contentValues.put(ITEMS_COLUMN_DATE, "");
        contentValues.put(ITEMS_COLUMN_IMAGE, "");
        contentValues.put(ITEMS_COLUMN_IMAGE_EXISTS, 0);
        contentValues.put(ITEMS_COLUMN_TWEET_POSTED, 0);
        db.insert(table_name, null, contentValues);
        return true;
    }

    public Cursor getPassword(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT password FROM users WHERE username='"+username+"'", null);
        return res;
    }

    public Cursor getData(int id, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String table_name = username + "_items";
        Cursor res = db.rawQuery("SELECT * FROM "+table_name+" WHERE id='"+id+"'", null);
        return res;
    }

    public Cursor getUserData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String table_name = "users";
        Cursor res = db.rawQuery("SELECT * FROM "+table_name+" WHERE username='"+username+"'", null);
        return res;
    }

    public int numberOfRows(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String table_name = username + "_items";
        int numRows = (int) DatabaseUtils.queryNumEntries(db, table_name);
        return  numRows;
    }

    public boolean updateItem (Integer id, String title, String description, String completed, double lat, double lon, String date, String img, String username, int tweet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String table_name = username + "_items";
        contentValues.put(ITEMS_COULMN_TITLE, title);
        contentValues.put(ITEMS_COLUMN_DESCRIPTION, description);
        contentValues.put(ITEMS_COLUMN_COMPLETED, completed);
        contentValues.put(ITEMS_COLUMN_LATITUDE, lat);
        contentValues.put(ITEMS_COLUMN_LONGITUDE, lon);
        contentValues.put(ITEMS_COLUMN_DATE, date);
        contentValues.put(ITEMS_COLUMN_TWEET_POSTED, tweet);
        db.update(table_name, contentValues, "id = ? ", new String[] { Integer.toString(id)} );
        return  true;
    }

    public boolean postTweet(Integer id, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table_name = username + "_items";
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEMS_COLUMN_TWEET_POSTED, 1);
        db.update(table_name, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return  true;

    }

    public boolean insertImage(Integer id, String username, Bitmap img) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table_name = username + "_items";
        ContentValues contentValues = new ContentValues();

        // convert Bitmap to Byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        contentValues.put(ITEMS_COLUMN_IMAGE, byteArray);
        contentValues.put(ITEMS_COLUMN_IMAGE_EXISTS, 1);
        db.update(table_name, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return  true;

    }

    public ArrayList<String> getAllItems(String username) {
        ArrayList<String> items = new ArrayList<String>();
        String table_name = username + "_items";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + table_name, null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            String title = res.getString(res.getColumnIndex(ITEMS_COULMN_TITLE));
            String completed = res.getString(res.getColumnIndex(ITEMS_COLUMN_COMPLETED));
            if (completed.equals("Completed!")) {
                title = "(completed) \t" + title;
            }
            else {
                title = "\t\t\t\t\t\t\t\t\t\t\t"+title;
            }
            items.add(title);
            res.moveToNext();
        }
        return items;
    }

}
