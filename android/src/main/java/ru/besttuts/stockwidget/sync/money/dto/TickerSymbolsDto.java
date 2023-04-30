package ru.besttuts.stockwidget.sync.money.dto;

import java.util.Collections;
import java.util.List;

public class TickerSymbolsDto {
    private List<Currency> currenciesFrom = Collections.emptyList();
    private List<Currency> currenciesTo = Collections.emptyList();
    private List<String> shares = Collections.emptyList();

    public List<Currency> getCurrenciesFrom() {
        return currenciesFrom;
    }

    public void setCurrenciesFrom(List<Currency> currenciesFrom) {
        this.currenciesFrom = currenciesFrom;
    }

    public List<Currency> getCurrenciesTo() {
        return currenciesTo;
    }

    public void setCurrenciesTo(List<Currency> currenciesTo) {
        this.currenciesTo = currenciesTo;
    }

    public List<String> getShares() {
        return shares;
    }

    public void setShares(List<String> shares) {
        this.shares = shares;
    }
}
