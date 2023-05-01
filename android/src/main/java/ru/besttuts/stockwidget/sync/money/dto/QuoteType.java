package ru.besttuts.stockwidget.sync.money.dto;

public enum QuoteType {
    CURRENCY(0),
    COMMODITY(1),
    STOCK_INDEX(2),
    STOCK(3), // SHARE
    BOND(4),
    CRYPTO(5);

    private final int numVal;

    QuoteType(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
