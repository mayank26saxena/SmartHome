package com.example.mayank.smarthome.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1 ;
    private static final String DATABASE_NAME = "Devices" ;
    public static final String TABLE_DEVICES = "devices" ;
    public static final String COLUMN_NAME = "name" ;
    public static final String COLUMN_LAST_CONNECTED = "last_connected" ;

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_SCORES_TABLE = "CREATE TABLE " + TABLE_DEVICES + "(" +
                COLUMN_NAME + " TEXT," +
                COLUMN_LAST_CONNECTED + " STRING" +
                ")" ;

        db.execSQL(CREATE_SCORES_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP_TABLE IF EXISTS " + TABLE_DEVICES);
        onCreate(db);

    }

    //Add a new row to the database

    public void addDevice(DeviceData deviceData){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, deviceData.getDeviceName());
        values.put(COLUMN_LAST_CONNECTED, deviceData.getLastConnectionTime());
        db.insert(TABLE_DEVICES, null, values) ;
        db.close();

    }

    //Print out the database as a String

    public List<DeviceData> getAllDevices() {
        List<DeviceData> deviceList = new ArrayList<DeviceData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                DeviceData deviceData = new DeviceData();
                deviceData.setDeviceName(cursor.getString(0));
                deviceData.setLastConnectionTime(cursor.getString(1));

                // Adding device data to list
                deviceList.add(deviceData);
            } while (cursor.moveToNext());
        }
        return deviceList;

    }

    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DEVICES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public Cursor getAllRooms(){

        String countQuery = "SELECT  * FROM " + TABLE_DEVICES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor ;
    }


}
