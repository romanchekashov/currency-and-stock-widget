package ru.besttuts.stockwidget.model;

/**
 * @author rchekashov
 *         created on 18.06.2016
 */
public class QuoteLastTradeDate implements Validable {
    private int _id;
    private String code;
    private String symbol;
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

    @Override
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
