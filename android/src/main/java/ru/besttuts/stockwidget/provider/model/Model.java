package ru.besttuts.stockwidget.provider.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.database.Cursor;

import ru.besttuts.stockwidget.provider.db.DbContract.ModelColumns;
import ru.besttuts.stockwidget.provider.db.DbContract.SettingColumns;

/**
 * Created by roman on 25.08.2018.
 */
@Entity(tableName = "models")
public class Model {

    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "id")
    protected String id; // symbol

    @ColumnInfo(name = "name")
    protected String name;

    @ColumnInfo(name = "rate")
    protected double rate = 0.0;

    @ColumnInfo(name = "change")
    protected double change = 0.0;

    @ColumnInfo(name = "currency")
    protected String currency;

    @ColumnInfo(name = "quote_type")
    private int quoteType;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public String getRateToString() {
        return String.valueOf((float)Math.round(rate*100)/100);
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getChange() {
        return change;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(int quoteType) {
        this.quoteType = quoteType;
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", rate=" + rate +
                ", change=" + change +
                '}';
    }

//    @Override
    public boolean isValid(){
        if (null == id) return false;
        if (null == name) return false;

        return true;
    }

    public static Model map(Cursor cursor){
        Model model = new Model();
        model.setId(cursor.getString(cursor.getColumnIndexOrThrow(ModelColumns.MODEL_ID)));
        model.setName(cursor.getString(cursor.getColumnIndexOrThrow(ModelColumns.MODEL_NAME)));
        model.setRate(cursor.getDouble(cursor.getColumnIndexOrThrow(ModelColumns.MODEL_RATE)));
        model.setChange(cursor.getDouble(cursor.getColumnIndexOrThrow(ModelColumns.MODEL_CHANGE)));
        model.setQuoteType(cursor.getInt(cursor.getColumnIndexOrThrow(SettingColumns.SETTING_QUOTE_TYPE)));
        try {
            model.setCurrency(cursor.getString(cursor.getColumnIndexOrThrow(ModelColumns.MODEL_CURRENCY)));
        } catch (IllegalArgumentException e) {
            // колонка 'model_currency' не существует у Валюты
        }

        return model;
    }
}
