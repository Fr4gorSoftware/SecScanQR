package de.t_dankworth.secscanqr.util;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Created by Thore Dankworth
 * Last Update: 20.05.2020
 * Last Update by Thore Dankworth
 *
 * This class represents an entity in the Android Room architecture which represents a table in the database.
 */

@Entity(tableName = "history_table", indices = {@Index(value = {"information"}, unique = true)})
public class HistoryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String format;

    @ColumnInfo(name = "information")
    private String information;

    private String date;

    public HistoryEntity(String format, String information, String date) {
        this.format = format;
        this.information = information;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFormat() {
        return format;
    }

    public String getInformation() {
        return information;
    }

    public String getDate() {
        return date;
    }
}
