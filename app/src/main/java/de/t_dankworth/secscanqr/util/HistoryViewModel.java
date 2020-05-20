package de.t_dankworth.secscanqr.util;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Created by Thore Dankworth
 * Last Update: 20.05.2020
 * Last Update by Thore Dankworth
 *
 * This class manages UI related data and keeps the data up to date with LiveData
 * It also communicates to the Repository which connects to the database.
 * Architecture: Model View ViewModel (MVVM)
 */

public class HistoryViewModel extends AndroidViewModel {

    private HistoryRepository historyRepository;
    private LiveData<List<HistoryEntity>> allItemsInHistory;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyRepository = new HistoryRepository(application);
        allItemsInHistory = historyRepository.getAllItemsInHistory();
    }

    public void insert(HistoryEntity historyEntity){
        historyRepository.insert(historyEntity);
    }

    public void delete(HistoryEntity historyEntity){
        historyRepository.delete(historyEntity);
    }

    public void deleteAllInHistory(){
        historyRepository.deleteAllInHistory();
    }

    public LiveData<List<HistoryEntity>> getAllItemsInHistory(){
        return allItemsInHistory;
    }
}
