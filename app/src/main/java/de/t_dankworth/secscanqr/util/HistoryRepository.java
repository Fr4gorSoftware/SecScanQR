package de.t_dankworth.secscanqr.util;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Created by Thore Dankworth
 * Last Update: 20.05.2020
 * Last Update by Thore Dankworth
 *
 * This class interact with the database if the ViewModel tells him to do so.
 * It implements all methods from the DAO to execute the actions on the database in an AsyncTask
 */

public class HistoryRepository {

    private HistoryDao historyDao;
    private LiveData<List<HistoryEntity>> allItemsInHistory;

    public HistoryRepository(Application application){
        HistoryDatabase database = HistoryDatabase.getInstance(application);
        historyDao = database.historyDao();
        allItemsInHistory = historyDao.getAllHistoryEntries();

    }

    public void insert(HistoryEntity historyEntity){
        new InsertInHistoryAsyncTask(historyDao).execute(historyEntity);
    }

    public void delete(HistoryEntity historyEntity){
        new DeleteInHistoryAsyncTask(historyDao).execute(historyEntity);
    }

    public void deleteAllInHistory(){
        new DeleteAllInHistoryAsyncTask(historyDao).execute();
    }

    /**
     * An Async Task to get all records in the history database
     */
    public LiveData<List<HistoryEntity>> getAllItemsInHistory(){
        return allItemsInHistory;
    }

    private static class InsertInHistoryAsyncTask extends AsyncTask<HistoryEntity, Void, Void> {

        private HistoryDao historyDao;

        private InsertInHistoryAsyncTask(HistoryDao historyDao){
            this.historyDao = historyDao;
        }

        @Override
        protected Void doInBackground(HistoryEntity... historyEntities) {
            historyDao.insert(historyEntities[0]);
            return null;
        }
    }

    /**
     * An Async Task to delete a specific record in the history database
     */
    private static class DeleteInHistoryAsyncTask extends AsyncTask<HistoryEntity, Void, Void> {

        private HistoryDao historyDao;

        private DeleteInHistoryAsyncTask(HistoryDao historyDao){
            this.historyDao = historyDao;
        }

        @Override
        protected Void doInBackground(HistoryEntity... historyEntities) {
            historyDao.delete(historyEntities[0]);
            return null;
        }
    }

    /**
     * An Async Task to delete all records in the history database
     */
    private static class DeleteAllInHistoryAsyncTask extends AsyncTask<Void, Void, Void> {

        private HistoryDao historyDao;

        private DeleteAllInHistoryAsyncTask(HistoryDao historyDao){
            this.historyDao = historyDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            historyDao.deleteAllInHistory();
            return null;
        }
    }
}
