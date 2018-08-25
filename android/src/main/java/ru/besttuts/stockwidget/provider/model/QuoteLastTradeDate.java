package ru.besttuts.stockwidget.provider.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by roman on 25.08.2018.
 */
@Entity(tableName = "quote_last_trade_dates")
public class QuoteLastTradeDate {

    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "symbol")
    private String symbol;

    @ColumnInfo(name = "last_trade_date")
    private long lastTradeDate;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getLastTradeDate() {
        return lastTradeDate;
    }

    public void setLastTradeDate(long lastTradeDate) {
        this.lastTradeDate = lastTradeDate;
    }

//    @Override
    public boolean isValid() {
        if (null == code) return false;
        if (null == symbol) return false;

        return true;
    }

    @Override
    public String toString() {
        return "QuoteLastTradeDate{" +
                "_id=" + _id +
                ", code='" + code + '\'' +
                ", symbol='" + symbol + '\'' +
                ", lastTradeDate=" + lastTradeDate +
                '}';
    }
}
