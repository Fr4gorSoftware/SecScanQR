package de.t_dankworth.secscanqr.util;
;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Thore Dankworth
 * Last Update: 20.05.2020
 * Last Update by Thore Dankworth
 *
 * This class handles all operations regarding the old database used before version 1.2.7 for backwards compatibility. Not used functionality was removed.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SecScanQR.db";
    private static final String TABLE_SCANNED = "scanned";
    private static final String COLUMN_SCANNED_ID = "scanned_id";
    private static final String COLUMN_SCANNED_QRCODE = "code";

    private String CREATE_SCANNED_TABLE = "CREATE TABLE " + TABLE_SCANNED + "(" + COLUMN_SCANNED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SCANNED_QRCODE + " TEXT)";

    private String DROP_SCANNED_TABLE = "DROP TABLE IF EXISTS " + TABLE_SCANNED;


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
     * Returns all the data from the database
     * @return a Cursor pointing on the requested table
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_SCANNED + " ORDER BY " + COLUMN_SCANNED_ID;
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
