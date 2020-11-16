package ru.besttuts.stockwidget.provider.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by roman on 25.08.2018.
 */
@Entity(tableName = "models")
@ToString
public class Model {

    @PrimaryKey
    @Getter @Setter
    private int id;

    @ColumnInfo(name = "symbol")
    @Getter @Setter
    private String symbol;

    @ColumnInfo(name = "name")
    @Getter @Setter
    protected String name;

    @ColumnInfo(name = "rate")
    @Getter @Setter
    protected double rate = 0.0;

    @ColumnInfo(name = "change")
    @Getter
    protected double change = 0.0;

    @ColumnInfo(name = "currency")
    @Getter @Setter
    protected String currency;

    @ColumnInfo(name = "quote_type")
    @Getter @Setter
    private int quoteType;

    @ColumnInfo(name = "quote_provider")
    @Getter @Setter
    private int quoteProvider;

    @ColumnInfo(name = "widget_id")
    @Getter @Setter
    private int widgetId;

    @ColumnInfo(name = "sort")
    @Getter @Setter
    private int sort;

    @ColumnInfo(name = "last_trade_date")
    @Getter @Setter
    private long lastTradeDate;

    public String getRateToString() {
        return String.valueOf((float)Math.round(rate*100)/100);
    }

    public String getChangeToString() {
        return String.valueOf((float)Math.round(change*10000)/10000);
    }

    public void setChange(double change) {
        this.change = (double)Math.round(change * 10000) / 10000;
    }

    public String getPercentChange() {
        if (0 != (int)(rate * 10000)) {
            double pChange = change * 100 / rate;
            return String.valueOf((float)Math.round(pChange * 10000) / 10000) + "%";
        }
        return "0.0%";
    }
}
