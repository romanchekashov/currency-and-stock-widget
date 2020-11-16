package ru.besttuts.stockwidget.provider.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.Data;

/**
 * Created by roman on 25.08.2018.
 */
@Entity(tableName = "quotes",
        indices = {@Index(value = {"symbol", "quote_type"}, unique = true)})
@Data
public class Quote {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "symbol")
    private String symbol;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "rate")
    private double rate = 0.0;

    @ColumnInfo(name = "change")
    private double change = 0.0;

    @ColumnInfo(name = "currency")
    private String currency;

    @ColumnInfo(name = "quote_type")
    private int quoteType;

    @ColumnInfo(name = "quote_provider")
    private int quoteProvider;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return id == quote.id &&
                symbol.equals(quote.symbol) &&
                quoteType == quote.quoteType;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
