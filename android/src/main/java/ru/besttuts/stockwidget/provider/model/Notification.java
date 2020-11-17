package ru.besttuts.stockwidget.provider.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

/**
 * Created by roman on 25.08.2018.
 */
@Entity(tableName = "notifications")
@Data
public class Notification {

    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "timestamp")
    private long timestamp;
}
