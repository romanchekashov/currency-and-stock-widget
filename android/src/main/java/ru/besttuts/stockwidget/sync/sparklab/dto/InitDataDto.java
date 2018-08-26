package ru.besttuts.stockwidget.sync.sparklab.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rchekashov on 26.08.2018.
 */
public class InitDataDto {
    private List<CurrencyInfoDto> currencies = new ArrayList<>();
    private List<CommodityInfoDto> commodities = new ArrayList<>();

    public InitDataDto() {
    }

    public List<CurrencyInfoDto> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CurrencyInfoDto> currencies) {
        this.currencies = currencies;
    }

    public List<CommodityInfoDto> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<CommodityInfoDto> commodities) {
        this.commodities = commodities;
    }
}
