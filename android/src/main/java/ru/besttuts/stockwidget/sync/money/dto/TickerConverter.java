package ru.besttuts.stockwidget.sync.money.dto;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.besttuts.stockwidget.model.Currency;
import ru.besttuts.stockwidget.model.Good;
import ru.besttuts.stockwidget.model.Model;


public class TickerConverter {
    private static final String TAG = makeLogTag(TickerConverter.class);

    public static synchronized Map<String, Model> convertToModelMap(List<QuoteDto> quotes){
        Map<String, Model> symbolModelMap = new HashMap<>();

        for (QuoteDto quote: quotes) {
            LOGD(TAG, quote.toString());
            if (QuoteType.CURRENCY.equals(quote.getType())) {
                symbolModelMap.put(quote.getSymbol(), readCurrency(quote));
            } else {
                symbolModelMap.put(quote.getSymbol(), readGood(quote));
            }
        }

        return symbolModelMap;
    }

    private static ru.besttuts.stockwidget.model.Currency readCurrency(QuoteDto rate) {
        ru.besttuts.stockwidget.model.Currency currency = new Currency();
        currency.setId(rate.getSymbol());
        currency.setName(rate.getName());

        if(null == rate.getLastPrice()){
            currency.setRate(0.0);
        } else {
            currency.setRate(rate.getLastPrice().doubleValue());
        }

        if(null == rate.getChange()){
            currency.setChange(0.0);
            currency.setPercentChange("0.0%");
        } else {
            currency.setChange(rate.getChange().doubleValue());
            currency.setPercentChange(rate.getChangeInPercent());
        }

        currency.setCurrency(rate.getCurrency());

        return currency;
    }

    private static Good readGood(QuoteDto quote) {
        Good good = new Good();
        good.setId(quote.getSymbol());
        good.setSymbol(quote.getSymbol());
        good.setName(quote.getName());

        if(null == quote.getLastPrice()){
            good.setRate(0.0);
        } else {
            good.setRate(quote.getLastPrice().doubleValue());
        }

        if(null == quote.getChange()){
            good.setChange(0.0);
            good.setPercentChange("0.0%");
        } else {
            good.setChange(quote.getChange().doubleValue());
            good.setPercentChange(quote.getChangeInPercent());
        }

        good.setCurrency(quote.getCurrency());

        return good;
    }
}
