package ru.besttuts.stockwidget.model;

import android.database.Cursor;

import ru.besttuts.stockwidget.provider.QuoteContract;

/**
 * @author rchekashov
 *         created on 11.03.2017
 */

public class SettingModel {
    String symbol;
    int quoteType;
    int quotePosition;
    Model model;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(int quoteType) {
        this.quoteType = quoteType;
    }

    public int getQuotePosition() {
        return quotePosition;
    }

    public void setQuotePosition(int quotePosition) {
        this.quotePosition = quotePosition;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public static SettingModel map(Cursor cursor) {
        SettingModel sm = new SettingModel();
        sm.setSymbol(cursor.getString(cursor.getColumnIndexOrThrow(
                QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL)));
        sm.setQuoteType(cursor.getInt(cursor.getColumnIndexOrThrow(
                QuoteContract.SettingColumns.SETTING_QUOTE_TYPE)));
        sm.setQuotePosition(cursor.getInt(cursor.getColumnIndexOrThrow(
                QuoteContract.SettingColumns.SETTING_QUOTE_POSITION)));
        sm.setModel(Model.map(cursor));
        return sm;
    }
}
