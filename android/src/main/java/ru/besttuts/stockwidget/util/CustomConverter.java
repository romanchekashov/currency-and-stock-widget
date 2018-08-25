package ru.besttuts.stockwidget.util;

import java.util.HashMap;
import java.util.Map;

import ru.besttuts.stockwidget.model.Currency;
import ru.besttuts.stockwidget.model.Good;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.sync.model.YahooMultiQueryData;
import ru.besttuts.stockwidget.sync.sparklab.QuoteDto;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 *         created on 06.10.2016
 */

public class CustomConverter {
    private static final String TAG = makeLogTag(CustomConverter.class);

    public static synchronized Map<String, Model> convertToModelMap(YahooMultiQueryData yahooMultiQueryData){
        Map<String, Model> symbolModelMap = new HashMap<>();
        for (YahooMultiQueryData.Rate rate: yahooMultiQueryData.rates){
//            symbolModelMap.put(rate.id, readCurrency(rate));
        }
        for (YahooMultiQueryData.Quote quote: yahooMultiQueryData.quotes){
//            symbolModelMap.put(quote.symbol, readGood(quote));
        }
        return symbolModelMap;
    }

    private static Currency readCurrency(YahooMultiQueryData.Rate rate) {
        Currency currency = new Currency();
        currency.setId(rate.id);
        if(null != rate.Name) {
            currency.setName(rate.Name.replace(" to ", "/"));
        }
        if(null != rate.Rate) currency.setRate(rate.Rate);

        try {
            Double ask = (null == rate.Ask) ? null : Double.parseDouble(rate.Ask);
            Double bid = (null == rate.Bid) ? null : Double.parseDouble(rate.Bid);
            if(null != ask && null != bid) currency.setChange(ask - bid);
        } catch (NumberFormatException e) {
            LOGE(TAG, e.getMessage());
        }

        return currency;
    }

    private static Good readGood(YahooMultiQueryData.Quote quote) {
        Good good = new Good();
        good.setId(quote.symbol);
        good.setSymbol(quote.symbol);

        if(null == quote.LastTradePriceOnly){
            good.setRate(0.0);
        } else {
            good.setRate(quote.LastTradePriceOnly);
        }

        if(null == quote.Change){
            good.setChange(0.0);
        } else {
            good.setChange(quote.Change);
        }
        good.setPercentChange(quote.ChangeinPercent);
        good.setName(quote.Name);
        good.setCurrency(quote.Currency);

        return good;
    }

    public static Model toModel(QuoteDto dto) {
        Model model = new Model();
        model.setId(dto.getSymbol());
        model.setChange(dto.getChange());
        model.setRate(dto.getRate());
        model.setCurrency(dto.getCurrency());
        model.setName(dto.getName());
        model.setQuoteType(dto.getType());
        return model;
    }
}
