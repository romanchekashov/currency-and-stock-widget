package ru.besttuts.stockwidget.sync.deserializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.besttuts.stockwidget.BuildConfig;
import ru.besttuts.stockwidget.sync.model.YahooMultiQueryData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author rchekashov
 *         created on 12.03.2017
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class YahooMultiQueryDataDeserializerTest {

    private final String MULTI_QUERY_JSON = "{\"query\":{\"count\":2,\"created\":\"2017-03-12T15:32:46Z\",\"lang\":\"ru-RU\",\"results\":{\"results\":[{\"rate\":[{\"id\":\"EURRUB\",\"Name\":\"EUR/RUB\",\"Rate\":\"63.1570\",\"Date\":\"3/10/2017\",\"Time\":\"10:25pm\",\"Ask\":\"63.1820\",\"Bid\":\"63.1570\"},{\"id\":\"USDRUB\",\"Name\":\"USD/RUB\",\"Rate\":\"58.8560\",\"Date\":\"3/10/2017\",\"Time\":\"10:30pm\",\"Ask\":\"58.9060\",\"Bid\":\"58.8560\"}]},{\"quote\":[{\"symbol\":\"NGZ15.NYM\",\"Ask\":null,\"AverageDailyVolume\":null,\"Bid\":null,\"AskRealtime\":null,\"BidRealtime\":null,\"BookValue\":null,\"Change_PercentChange\":null,\"Change\":null,\"Commission\":null,\"Currency\":null,\"ChangeRealtime\":null,\"AfterHoursChangeRealtime\":null,\"DividendShare\":null,\"LastTradeDate\":null,\"TradeDate\":null,\"EarningsShare\":null,\"ErrorIndicationreturnedforsymbolchangedinvalid\":null,\"EPSEstimateCurrentYear\":null,\"EPSEstimateNextYear\":null,\"EPSEstimateNextQuarter\":null,\"DaysLow\":null,\"DaysHigh\":null,\"YearLow\":null,\"YearHigh\":null,\"HoldingsGainPercent\":null,\"AnnualizedGain\":null,\"HoldingsGain\":null,\"HoldingsGainPercentRealtime\":null,\"HoldingsGainRealtime\":null,\"MoreInfo\":null,\"OrderBookRealtime\":null,\"MarketCapitalization\":null,\"MarketCapRealtime\":null,\"EBITDA\":null,\"ChangeFromYearLow\":null,\"PercentChangeFromYearLow\":null,\"LastTradeRealtimeWithTime\":null,\"ChangePercentRealtime\":null,\"ChangeFromYearHigh\":null,\"PercebtChangeFromYearHigh\":null,\"LastTradeWithTime\":null,\"LastTradePriceOnly\":null,\"HighLimit\":null,\"LowLimit\":null,\"DaysRange\":null,\"DaysRangeRealtime\":null,\"FiftydayMovingAverage\":null,\"TwoHundreddayMovingAverage\":null,\"ChangeFromTwoHundreddayMovingAverage\":null,\"PercentChangeFromTwoHundreddayMovingAverage\":null,\"ChangeFromFiftydayMovingAverage\":null,\"PercentChangeFromFiftydayMovingAverage\":null,\"Name\":null,\"Notes\":null,\"Open\":null,\"PreviousClose\":null,\"PricePaid\":null,\"ChangeinPercent\":null,\"PriceSales\":null,\"PriceBook\":null,\"ExDividendDate\":null,\"PERatio\":null,\"DividendPayDate\":null,\"PERatioRealtime\":null,\"PEGRatio\":null,\"PriceEPSEstimateCurrentYear\":null,\"PriceEPSEstimateNextYear\":null,\"Symbol\":\"NGZ15.NYM\",\"SharesOwned\":null,\"ShortRatio\":null,\"LastTradeTime\":null,\"TickerTrend\":null,\"OneyrTargetPrice\":null,\"Volume\":null,\"HoldingsValue\":null,\"HoldingsValueRealtime\":null,\"YearRange\":null,\"DaysValueChange\":null,\"DaysValueChangeRealtime\":null,\"StockExchange\":null,\"DividendYield\":null,\"PercentChange\":null},{\"symbol\":\"BZF16.NYM\",\"Ask\":null,\"AverageDailyVolume\":null,\"Bid\":null,\"AskRealtime\":null,\"BidRealtime\":null,\"BookValue\":null,\"Change_PercentChange\":null,\"Change\":null,\"Commission\":null,\"Currency\":null,\"ChangeRealtime\":null,\"AfterHoursChangeRealtime\":null,\"DividendShare\":null,\"LastTradeDate\":null,\"TradeDate\":null,\"EarningsShare\":null,\"ErrorIndicationreturnedforsymbolchangedinvalid\":null,\"EPSEstimateCurrentYear\":null,\"EPSEstimateNextYear\":null,\"EPSEstimateNextQuarter\":null,\"DaysLow\":null,\"DaysHigh\":null,\"YearLow\":null,\"YearHigh\":null,\"HoldingsGainPercent\":null,\"AnnualizedGain\":null,\"HoldingsGain\":null,\"HoldingsGainPercentRealtime\":null,\"HoldingsGainRealtime\":null,\"MoreInfo\":null,\"OrderBookRealtime\":null,\"MarketCapitalization\":null,\"MarketCapRealtime\":null,\"EBITDA\":null,\"ChangeFromYearLow\":null,\"PercentChangeFromYearLow\":null,\"LastTradeRealtimeWithTime\":null,\"ChangePercentRealtime\":null,\"ChangeFromYearHigh\":null,\"PercebtChangeFromYearHigh\":null,\"LastTradeWithTime\":null,\"LastTradePriceOnly\":null,\"HighLimit\":null,\"LowLimit\":null,\"DaysRange\":null,\"DaysRangeRealtime\":null,\"FiftydayMovingAverage\":null,\"TwoHundreddayMovingAverage\":null,\"ChangeFromTwoHundreddayMovingAverage\":null,\"PercentChangeFromTwoHundreddayMovingAverage\":null,\"ChangeFromFiftydayMovingAverage\":null,\"PercentChangeFromFiftydayMovingAverage\":null,\"Name\":null,\"Notes\":null,\"Open\":null,\"PreviousClose\":null,\"PricePaid\":null,\"ChangeinPercent\":null,\"PriceSales\":null,\"PriceBook\":null,\"ExDividendDate\":null,\"PERatio\":null,\"DividendPayDate\":null,\"PERatioRealtime\":null,\"PEGRatio\":null,\"PriceEPSEstimateCurrentYear\":null,\"PriceEPSEstimateNextYear\":null,\"Symbol\":\"BZF16.NYM\",\"SharesOwned\":null,\"ShortRatio\":null,\"LastTradeTime\":null,\"TickerTrend\":null,\"OneyrTargetPrice\":null,\"Volume\":null,\"HoldingsValue\":null,\"HoldingsValueRealtime\":null,\"YearRange\":null,\"DaysValueChange\":null,\"DaysValueChangeRealtime\":null,\"StockExchange\":null,\"DividendYield\":null,\"PercentChange\":null}]}]}}}";
    YahooMultiQueryDataDeserializer deserializer = new YahooMultiQueryDataDeserializer();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void deserialize_shouldWork(){
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(MULTI_QUERY_JSON).getAsJsonObject();

        YahooMultiQueryData yahooData = deserializer.deserialize(jsonObject, null, null);
        assertEquals(2, yahooData.rates.size());
//        assertEquals(2, yahooData.quotes.size());

        for (YahooMultiQueryData.Rate rate: yahooData.rates){
            assertNotNull(rate.id);
            assertNotNull(rate.Name);
            assertNotNull(rate.Rate);
            System.out.println(rate.toString());
        }
    }
}
