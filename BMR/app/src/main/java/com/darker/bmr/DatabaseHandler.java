package com.darker.bmr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darker on 24/12/59.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // Version
    private static final int DB_VER = 1;
    // Database Name
    private static final String DB_NAME = "BMRManager6";
    // Table Name
    private static final String TABLE_BMR = "bmrs";
    // Column
    private static final String ID = "id", TIME = "time", BMR = "bmr";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context){
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql = "CREATE TABLE " + TABLE_BMR + "(" + ID + " INTEGER PRIMARY KEY, "
                    + TIME + " TEXT, " + BMR + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMR);
        onCreate(db);
    }

    public void addBMR(BMR bmr){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TIME, bmr.getTime());
        values.put(BMR, bmr.getBmr());

        db.insert(TABLE_BMR, null, values);
        db.close();
    }

    public BMR getBMR(int id) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BMR, new String[] { ID, TIME, BMR }, ID + "=?",
                        new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        BMR bmr = new BMR(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));

        db.close();
        return bmr;
    }

    public int getBMRCount() {
        String bmrQuery = "SELECT * FROM " + TABLE_BMR;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(bmrQuery, null);

        return cursor.getCount();
    }

    public List<BMR> getAllBMR() {
        List<BMR> bmrList = new ArrayList<BMR>();
        String selectQuery = "SELECT * FROM " + TABLE_BMR;
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BMR bmr = new BMR();
                bmr.setId(Integer.parseInt(cursor.getString(0)));
                bmr.setTime(cursor.getString(1));
                bmr.setBmr(cursor.getString(2));

                bmrList.add(bmr);
            } while (cursor.moveToNext());
        }

        db.close();
        return bmrList;
    }

    public void deleteBMR(BMR bmr) {
        db = this.getWritableDatabase();
        db.delete(TABLE_BMR, ID + " =?", new String[] { String.valueOf(bmr.getId()) });
        db.close();
    }

    public void delTable(){
        onUpgrade(this.getWritableDatabase(), 1, 2);
    }

    public void resetId(){
        db = this.getWritableDatabase();
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME = '" + TABLE_BMR + "'");
        db.close();
    }
}
