package ru.besttuts.stockwidget.model;


import android.database.Cursor;

import ru.besttuts.stockwidget.provider.QuoteContract;

public class Quote {
    private int _id;
    private String symbol;
    private String name;
    private int quoteType;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(int quoteType) {
        this.quoteType = quoteType;
    }

    public static Quote map(Cursor cursor) {
        Quote setting = new Quote();
        setting.set_id(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.QuoteColumns._ID)));
        setting.setSymbol(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.QuoteColumns.QUOTE_SYMBOL)));
        setting.setName(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.QuoteColumns.QUOTE_NAME)));
        setting.setQuoteType(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.QuoteColumns.QUOTE_TYPE)));
        return setting;
    }
}
