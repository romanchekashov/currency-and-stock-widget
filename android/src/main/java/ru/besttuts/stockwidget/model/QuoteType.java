package ru.besttuts.stockwidget.model;

/**
 * Created by roman on 15.01.2015.
 */
public enum QuoteType {
    CURRENCY_EXCHANGE(0), GOODS(1), STOCK(2), INDICES(3), QUOTES(4);

    private final int value;

    private QuoteType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
