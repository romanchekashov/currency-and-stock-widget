package ru.besttuts.stockwidget.provider.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

/**
 * Created by roman on 25.08.2018.
 */
@Entity(tableName = "settings")
@Data
public class Setting {

    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "widget_id")
    private int widgetId;

    @ColumnInfo(name = "quote_position")
    private int quotePosition;

    @ColumnInfo(name = "quote_type")
    private int quoteType;

    @ColumnInfo(name = "quote_symbol")
    private String quoteSymbol;

    @ColumnInfo(name = "last_trade_date")
    private long lastTradeDate;
}
