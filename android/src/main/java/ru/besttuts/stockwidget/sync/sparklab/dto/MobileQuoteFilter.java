package ru.besttuts.stockwidget.sync.sparklab.dto;

import java.util.Set;

public class MobileQuoteFilter {
    private Set<Integer> currencies;
    private Set<Integer> stocks;
    private Set<Integer> futures;

    public Set<Integer> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Set<Integer> currencies) {
        this.currencies = currencies;
    }

    public Set<Integer> getStocks() {
        return stocks;
    }

    public void setStocks(Set<Integer> stocks) {
        this.stocks = stocks;
    }

    public Set<Integer> getFutures() {
        return futures;
    }

    public void setFutures(Set<Integer> futures) {
        this.futures = futures;
    }
}
