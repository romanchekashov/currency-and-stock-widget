package ru.besttuts.stockwidget.provider.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by roman on 25.08.2018.
 */
@Entity(tableName = "quotes")
public class Quote {

    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "quote_symbol")
    private String quoteSymbol;

    @ColumnInfo(name = "quote_name")
    private String quoteName;

    @ColumnInfo(name = "quote_type")
    private String quoteType;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getQuoteSymbol() {
        return quoteSymbol;
    }

    public void setQuoteSymbol(String quoteSymbol) {
        this.quoteSymbol = quoteSymbol;
    }

    public String getQuoteName() {
        return quoteName;
    }

    public void setQuoteName(String quoteName) {
        this.quoteName = quoteName;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "_id=" + _id +
                ", quoteSymbol='" + quoteSymbol + '\'' +
                ", quoteName='" + quoteName + '\'' +
                ", quoteType='" + quoteType + '\'' +
                '}';
    }
}
