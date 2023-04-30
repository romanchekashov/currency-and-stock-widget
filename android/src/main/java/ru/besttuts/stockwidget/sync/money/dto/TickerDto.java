package ru.besttuts.stockwidget.sync.money.dto;

public class TickerDto {
    public String symbol;
    public String name;
    public Double lastTradePrice;
    public Double change;
    public String changeInPercent; // 0.0%
    public String currency;
}
