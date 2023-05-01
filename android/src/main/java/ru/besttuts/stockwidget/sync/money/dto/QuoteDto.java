package ru.besttuts.stockwidget.sync.money.dto;

import java.math.BigDecimal;

public class QuoteDto {
    private String symbol;
    private String name;
    private BigDecimal lastPrice;
    private BigDecimal change;
    private String changeInPercent;
    private String currency;
    private QuoteType type;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public String getChangeInPercent() {
        return changeInPercent;
    }

    public void setChangeInPercent(String changeInPercent) {
        this.changeInPercent = changeInPercent;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public QuoteType getType() {
        return type;
    }

    public void setType(QuoteType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "QuoteDto{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", lastPrice=" + lastPrice +
                ", change=" + change +
                ", changeInPercent='" + changeInPercent + '\'' +
                ", currency='" + currency + '\'' +
                ", type=" + type +
                '}';
    }
}
