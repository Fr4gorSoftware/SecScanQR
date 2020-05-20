package de.t_dankworth.secscanqr.util;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Created by Thore Dankworth
 * Last Update: 20.05.2020
 * Last Update by Thore Dankworth
 *
 * This interface defines the available database interactions
 * Dao = Data Access Objects
 */

@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HistoryEntity history);

    @Delete
    void delete(HistoryEntity history);

    @Query("DELETE FROM history_table")
    void deleteAllInHistory();

    @Query("SELECT * FROM history_table ORDER BY id DESC")
    LiveData<List<HistoryEntity>> getAllHistoryEntries();
}
