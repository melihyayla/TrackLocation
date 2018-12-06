package com.servet.tracklocation.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Tracked-Locations";

    private static final String TABLE_NAME = "location_list";
    private static String LOCATION_TITLE = "location_title";
    private static String LOCATION_ID = "id";
    private static String LATITUDE = "latitude";
    private static String LONGITUDE = "longitude";
    private static String LOCATION_TIME = "location_time";
    private static String LOCATION_DATE = "location_date";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LOCATION_TITLE + " TEXT,"
                + LATITUDE + " TEXT,"
                + LONGITUDE + " TEXT,"
                + LOCATION_TIME + " TEXT,"
                + LOCATION_DATE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    public void deleteLocation(int id){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, LOCATION_ID + " = ?",
                new String[] { String.valueOf(id) });

        Log.i("DB-DELETE", "Deleted");

        db.close();
    }

    public void addLocation(String title, String longitude, String latitude, String location_time, String location_date) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOCATION_TITLE, title);
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        values.put(LOCATION_TIME, location_time);
        values.put(LOCATION_DATE, location_date);


        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public HashMap<String, String> locationDetail(int id){

        HashMap<String,String> devices = new HashMap<String,String>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME+ " WHERE id="+id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            devices.put(LOCATION_TITLE, cursor.getString(1));
            devices.put(LATITUDE, cursor.getString(2));
            devices.put(LONGITUDE, cursor.getString(3));
            devices.put(LOCATION_TIME, cursor.getString(4));
            devices.put(LOCATION_DATE, cursor.getString(5));

        }
        cursor.close();
        db.close();

        return devices;
    }

    public ArrayList<HashMap<String, String>> activeLocation(){

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<HashMap<String, String>> deviceList = new ArrayList<HashMap<String, String>>();

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for(int i=0; i<cursor.getColumnCount();i++)
                {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                deviceList.add(map);
            } while (cursor.moveToNext());
        }
        db.close();

        return deviceList;
    }

    public void editLocation(String location_title, String longitude , String latitude , String location_time , String location_date , int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LOCATION_TITLE, location_title);
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        values.put(LOCATION_TIME, location_time);
        values.put(LOCATION_DATE, location_date);

        db.update(TABLE_NAME, values, LOCATION_ID + " = ?",
                new String[] { String.valueOf(id) });
    }



    public int getRowCount() {

        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        return rowCount;
    }


    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }
}
