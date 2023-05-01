package ru.besttuts.stockwidget.sync.money.dto;

public enum QuoteType {
    CURRENCY(0),
    STOCK_INDEX(1),
    COMMODITY(2), // SHARE
    BOND(3),
    CRYPTO(4),
    STOCK(5);

    private final int numVal;

    QuoteType(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
