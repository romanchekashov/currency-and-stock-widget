package ru.besttuts.stockwidget.provider.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.database.Cursor;

import ru.besttuts.stockwidget.provider.QuoteContract;

/**
 * Created by roman on 25.08.2018.
 */
@Entity(tableName = "settings")
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

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public int getQuotePosition() {
        return quotePosition;
    }

    public void setQuotePosition(int quotePosition) {
        this.quotePosition = quotePosition;
    }

    public int getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(int quoteType) {
        this.quoteType = quoteType;
    }

    public String getQuoteSymbol() {
        return quoteSymbol;
    }

    public void setQuoteSymbol(String quoteSymbol) {
        if (null != quoteSymbol) {
            this.quoteSymbol = quoteSymbol.toUpperCase();
        } else {
            this.quoteSymbol = quoteSymbol;
        }
    }

    public long getLastTradeDate() {
        return lastTradeDate;
    }

    public void setLastTradeDate(long lastTradeDate) {
        this.lastTradeDate = lastTradeDate;
    }

    @Override
    public String toString() {
        return "Setting{" +
                " _id='" + _id + '\'' +
                ", id='" + id + '\'' +
                ", widgetId=" + widgetId +
                ", quotePosition=" + quotePosition +
                ", quoteType=" + quoteType +
                ", quoteSymbol='" + quoteSymbol + '\'' +
                ", lastTradeDate=" + lastTradeDate +
                '}';
    }

//    @Override
    public boolean isValid() {
        if (null == id) return false;
        if (widgetId == 0) return false;
        if (quotePosition == 0) return false;
        if (null == quoteSymbol) return false;

        return true;
    }

    public static Setting map(Cursor cursor) {
        Setting setting = new Setting();
        setting.set_id(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.Settings._ID)));
        setting.setId(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_ID)));
        setting.setWidgetId(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_WIDGET_ID)));
        setting.setQuotePosition(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_POSITION)));
        setting.setQuoteType(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_TYPE)));
        setting.setQuoteSymbol(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL)));
        setting.setLastTradeDate(cursor.getLong(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.LAST_TRADE_DATE)));

        return setting;
    }
}
