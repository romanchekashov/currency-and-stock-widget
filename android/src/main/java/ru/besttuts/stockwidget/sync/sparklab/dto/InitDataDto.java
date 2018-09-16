package ru.besttuts.stockwidget.sync.sparklab.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Created by rchekashov on 26.08.2018.
 */
@Data
public class InitDataDto {
    private List<CurrencyInfoDto> currencies = new ArrayList<>();
    private List<CommodityInfoDto> commodities = new ArrayList<>();
    private List<QuoteProviderDto> quoteProviders = new ArrayList<>();
}
