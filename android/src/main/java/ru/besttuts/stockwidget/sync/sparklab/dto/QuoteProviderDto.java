package ru.besttuts.stockwidget.sync.sparklab.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author romanchekashov
 * @since 22.10.2016
 */
@Data
public class QuoteProviderDto {
    private String provider;
    private String providerName;
    private List<CurrencyInfoDto> currencies = new ArrayList<>();
}
