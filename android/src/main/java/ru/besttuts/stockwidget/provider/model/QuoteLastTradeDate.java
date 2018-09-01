package ru.besttuts.stockwidget.provider.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.Data;

/**
 * Created by roman on 25.08.2018.
 */
@Entity(tableName = "quote_last_trade_dates")
@Data
public class QuoteLastTradeDate {

    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "symbol")
    private String symbol;

    @ColumnInfo(name = "last_trade_date")
    private long lastTradeDate;
}
