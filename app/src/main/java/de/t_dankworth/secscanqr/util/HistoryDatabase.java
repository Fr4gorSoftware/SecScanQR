package de.t_dankworth.secscanqr.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Thore Dankworth
 * Last Update: 20.05.2020
 * Last Update by Thore Dankworth
 *
 * This class is the Room database for the history itself. It returns an instance of an existing history database or return a new instance
 */

@Database(entities = {HistoryEntity.class}, version = 1)
public abstract class HistoryDatabase extends RoomDatabase {

    private static HistoryDatabase instance;
    private static DatabaseHelper historyDatabaseHelper;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public abstract HistoryDao historyDao();

    public static synchronized HistoryDatabase getInstance(Context context){
        if (instance == null){
            historyDatabaseHelper = new DatabaseHelper(context);
            instance = Room.databaseBuilder(context.getApplicationContext(), HistoryDatabase.class, "history_database").fallbackToDestructiveMigration().addCallback(roomCallback).build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new MigrateOldDbAsyncTask(instance).execute();

        }
    };

    /**
     * This AsyncTask will migrate the old history database into the new one if it exists.
     */
    private static class MigrateOldDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private HistoryDao historyDao;

        private MigrateOldDbAsyncTask(HistoryDatabase db){
            historyDao = db.historyDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Cursor data = historyDatabaseHelper.getData();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            while(data.moveToNext()) {
                historyDao.insert(new HistoryEntity("UNKNOWN", data.getString(1), date.format(timestamp)));
            }
            return null;
        }
    }
}
