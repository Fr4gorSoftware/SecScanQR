package de.t_dankworth.secscanqr.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Thore Dankworth
 * Last Update: 25.04.2018
 * Last Update by Thore Dankworth
 *
 * This class handles all operations regarding the sql database
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SecScanQR.db";
    private static final String TABLE_SCANNED = "scanned";
    private static final String COLUMN_SCANNED_ID = "scanned_id";
    private static final String COLUMN_SCANNED_QRCODE = "code";

    private String CREATE_SCANNED_TABLE = "CREATE TABLE " + TABLE_SCANNED + "(" + COLUMN_SCANNED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SCANNED_QRCODE + " TEXT)";

    private String DROP_SCANNED_TABLE = "DROP TABLE IF EXISTS" + TABLE_SCANNED;


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_SCANNED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL(DROP_SCANNED_TABLE);
        onCreate(db);
    }

    /**
     * This method adds a scanned qr code
     * @param code = qrcode that will be added to the database
     * @return a boolean if the code was added to the database or not
     */
    public boolean addData(String code){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCANNED_QRCODE, code);
        //Insert Data into Database with a checking if the insert into the database worked
        long result = db.insert(TABLE_SCANNED, null, values);
        if(result == -1){
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }


    /**
     * Returns all the data from the database
     * @return a Cursor pointing on the requested table
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_SCANNED;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Returns the ID of the scanned code given as a parameter
     * @param code
     * @return a Cursor pointing on the id
     */
    public Cursor getItemID(String code){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COLUMN_SCANNED_ID + " FROM " + TABLE_SCANNED + " WHERE " + COLUMN_SCANNED_QRCODE + " = '" + code + "'";
        Cursor data = db.rawQuery(query, null);
        return  data;

    }

    /**
     * Delete a specific Item from database
     * @param id
     */
    public void deleteItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_SCANNED + " WHERE " + COLUMN_SCANNED_ID + " = '" + id + "'";
        db.execSQL(query);
    }

    /**
     * Reset database
     */
    public void resetDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_SCANNED;
        db.execSQL(query);
    }


}
