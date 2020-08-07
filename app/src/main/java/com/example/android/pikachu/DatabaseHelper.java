package com.example.android.pikachu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.android.pikachu.AlarmList;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "medicine";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MINUTE = "minute";
    private static final String KEY_EVERYDAY = "everyday";
    private static final String KEY_MEDICINE_NAME = "medicineName";
    private static final String KEY_DOSE_QUANTITY = "dose_quantity";
    private static final String KEY_DOSE_QUALITY = "dose_units";
    private static final String KEY_ALARM_ID = "alarm_id";
    private static final String KEY_SUNDAY = "sunday";
    private static final String KEY_MONDAY = "monday";
    private static final String KEY_TUESDAY = "tuesday";
    private static final String KEY_WEDNESDAY = "wednesday";
    private static final String KEY_THURSDAY = "thursday";
    private static final String KEY_FRIDAY = "friday";
    private static final String KEY_SATURDAY = "saturday";
    private static final String KEY_STATE = "state";


    private static final String DATABASE_NAME = "MedicineAlarm.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_MEDICINE_NAME + " TEXT, " + KEY_HOUR + " INTEGER, " + KEY_MINUTE + " INTEGER, " + KEY_DOSE_QUANTITY + " TEXT, " +
                KEY_DOSE_QUALITY + " TEXT, " + KEY_EVERYDAY + " INTEGER, " + KEY_SUNDAY + " INTEGER, " + KEY_MONDAY + " INTEGER, " +
                KEY_TUESDAY + " INTEGER, " + KEY_WEDNESDAY + " INTEGER, " + KEY_THURSDAY + " INTEGER, " + KEY_FRIDAY + " INTEGER, "
                + KEY_SATURDAY + " INTEGER, " +KEY_STATE+ " INTEGER"+ ");";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(AlarmList alarmList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("id",1);
        contentValues.put(KEY_MEDICINE_NAME, alarmList.getMedicineName());
        contentValues.put(KEY_HOUR, alarmList.getHour());
        contentValues.put(KEY_MINUTE, alarmList.getMinute());
        contentValues.put(KEY_DOSE_QUANTITY, alarmList.getQuantity());
        contentValues.put(KEY_DOSE_QUALITY, alarmList.getQuality());
        contentValues.put(KEY_EVERYDAY, alarmList.getEveryday());
        contentValues.put(KEY_SUNDAY, alarmList.getSunday());
        contentValues.put(KEY_MONDAY, alarmList.getMonday());
        contentValues.put(KEY_TUESDAY, alarmList.getTuesday());
        contentValues.put(KEY_WEDNESDAY, alarmList.getWednesday());
        contentValues.put(KEY_THURSDAY, alarmList.getThursday());
        contentValues.put(KEY_FRIDAY, alarmList.getFriday());
        contentValues.put(KEY_SATURDAY, alarmList.getSaturday());
        contentValues.put(KEY_STATE, alarmList.getState());

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemID(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT id FROM " + TABLE_NAME +
                " WHERE " + KEY_MEDICINE_NAME + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void updateName(AlarmList alarmList, int id, String oldName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_MEDICINE_NAME +
                " = '" + alarmList.getMedicineName() + "'," +
                KEY_HOUR + "=" + alarmList.getHour() + "," +
                KEY_MINUTE + "=" + alarmList.getMinute() + "," +
                KEY_DOSE_QUANTITY + "= '" + alarmList.getQuantity() + "'," +
                KEY_DOSE_QUALITY + "= '" + alarmList.getQuality() + "'," +
                KEY_EVERYDAY + "=" + alarmList.getEveryday() + "," +
                KEY_SUNDAY + "=" + alarmList.getSunday() + "," +
                KEY_MONDAY + "=" + alarmList.getMonday() + "," +
                KEY_TUESDAY + "=" + alarmList.getTuesday() + "," +
                KEY_WEDNESDAY + "=" + alarmList.getWednesday() + "," +
                KEY_THURSDAY + "=" + alarmList.getThursday() + "," +
                KEY_FRIDAY + "=" + alarmList.getFriday() + "," +
                KEY_SATURDAY + "=" + alarmList.getSaturday() +
                " WHERE id = '" + id + "'" +
                " AND " + KEY_MEDICINE_NAME + " = '" + oldName + "'";
        Log.d("UPDATE DATA", "updateName: query: " + query);
        Log.d("UPDATE DATA", "updateName: Setting name to " + alarmList.getMedicineName());
        db.execSQL(query);
    }

    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE id = '" + id + "'" +
                " AND " + KEY_MEDICINE_NAME + " = '" + name + "'";
        Log.d("DELETE DATA", "deleteName: query: " + query);
        Log.d("DELETE DATA", "deleteName: Deleting " + name + " from database.");
        db.execSQL(query);
    }

    public void updateStateValue(int id,String name,int state){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET "+KEY_STATE+
                "="+state+  " WHERE id = '" + id + "'" +
                " AND " + KEY_MEDICINE_NAME + " = '" + name + "'";
        db.execSQL(query);
    }

}
