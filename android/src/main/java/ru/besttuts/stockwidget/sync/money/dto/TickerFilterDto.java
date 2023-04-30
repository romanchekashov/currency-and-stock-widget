package ru.besttuts.stockwidget.sync.money.dto;

import java.util.Collections;
import java.util.List;

public class TickerFilterDto {
    private List<String> symbols = Collections.emptyList();

    public List<String> getSymbols() {
        return symbols;
    }

    public TickerFilterDto setSymbols(List<String> symbols) {
        this.symbols = symbols;
        return this;
    }
}
