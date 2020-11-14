package ru.besttuts.stockwidget.provider.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

/**
 * Created by roman on 25.08.2018.
 */
@Entity(tableName = "quotes")
@Data
public class Quote {

    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "quote_symbol")
    private String quoteSymbol;

    @ColumnInfo(name = "quote_name")
    private String quoteName;

    @ColumnInfo(name = "quote_type")
    private String quoteType;
}
