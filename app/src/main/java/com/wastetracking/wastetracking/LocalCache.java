package com.wastetracking.wastetracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Tony on 11/13/2017.
 */

public class LocalCache extends SQLiteOpenHelper {
    private static final String LOG_TAG = "LocalCache";

    private static final String DATABASE_NAME = "LocalCache.db";
    private static final String TABLE_NAME = "scanned_recording";
    private static final String TABLE_COLUMN_KEY = "time";
    private static final String TABLE_COLUMN_KEY_TYPE = "text";
    private static final String TABLE_COLUMN_VALUE = "recorded_data";
    private static final String TABLE_COLUMN_VALUE_TYPE = "text";


    public LocalCache(Context context){
        super (context, DATABASE_NAME, null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryString = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + " ("
                + TABLE_COLUMN_KEY + " " + TABLE_COLUMN_KEY_TYPE + ", "
                + TABLE_COLUMN_VALUE + " " + TABLE_COLUMN_VALUE_TYPE + ")";

        Log.d(LOG_TAG, queryString);
        sqLiteDatabase.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String queryString = "DROP TABLE IF EXISTS " + TABLE_NAME;

        Log.d(LOG_TAG, queryString);
        sqLiteDatabase.execSQL(queryString);
    }

    public void insertEntry(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TABLE_COLUMN_KEY, key);
        contentValues.put(TABLE_COLUMN_VALUE, value);

        db.insert(TABLE_NAME, null,contentValues);
    }

    public ArrayList<String> getAllEntries() {
        ArrayList<String> allEntries = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            String extractedKey = res.getString(res.getColumnIndex(TABLE_COLUMN_KEY));
            String extractedValue = res.getString(res.getColumnIndex(TABLE_COLUMN_VALUE));
            allEntries.add(extractedKey + ", " + extractedValue);

            res.moveToNext();
        }

        res.close();

        return allEntries;
    }
}
