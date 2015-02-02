package ru.besttuts.stockwidget.model;

/**
 * Created by roman on 05.01.2015.
 */
public class Currency extends Model {
    private String date;
    private String time;
    private String ask;
    private String bid;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    @Override
    public String toString() {
        return String.format("Currency{%s}", super.toString());
    }
}
