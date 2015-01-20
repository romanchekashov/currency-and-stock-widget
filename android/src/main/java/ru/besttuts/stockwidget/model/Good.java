package ru.besttuts.stockwidget.model;

import java.util.Date;

/**
 * Created by roman on 06.01.2015.
 */
public class Good extends Model {
    private String symbol;
    private String name;
    private String bid;
    private Date date;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("Good{%s}", super.toString());
    }

}
