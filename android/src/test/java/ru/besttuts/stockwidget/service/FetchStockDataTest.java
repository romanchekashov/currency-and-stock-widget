package ru.besttuts.stockwidget.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.besttuts.stockwidget.BuildConfig;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;
import ru.besttuts.stockwidget.sync.model.YahooMultiQueryData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author rchekashov
 *         created on 16.03.2017
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class FetchStockDataTest {

    private int[] WIDGET_IDS = new int[]{1,2,3};
    private int WIDGET_ID = 1;
    final String[] DEFAULT_COMMODITIES = new String[]{"BZF16.NYM", "NGZ15.NYM", "GCX15.CMX"};

    private RemoteYahooFinanceDataFetcher dataFetcherMock = mock(RemoteYahooFinanceDataFetcher.class);
    private DbProvider dbProviderMock = mock(DbProvider.class);

    @Before
    public void setUp() throws Exception {
        DbProvider.init(RuntimeEnvironment.application);
    }

    @Test
    public void fetch_shouldReturnQuoteData() throws IOException {
        List<Setting> settings = generateSettings();
        when(dbProviderMock.getAllSettingsWithCheck()).thenReturn(settings);

        YahooMultiQueryData yahooMultiQueryData = generateYahooMultiQueryData(settings);
        when(dataFetcherMock.getYahooMultiQueryData()).thenReturn(yahooMultiQueryData);

        FetchStockData fetchStockData = new FetchStockData(WIDGET_IDS, true, dataFetcherMock, dbProviderMock);
        Map<Integer, List<Model>> mapWidgetIdWithQuotes = fetchStockData.fetch();
        assertEquals(1, mapWidgetIdWithQuotes.size());

        for (Integer widgetId: WIDGET_IDS){
            List<Model> models = mapWidgetIdWithQuotes.get(widgetId);
            if(WIDGET_ID == widgetId){
                assertEquals(DEFAULT_COMMODITIES.length, models.size());
            } else {
                assertNull(models);
            }
        }
    }

    private List<Setting> generateSettings(){
        List<Setting> settings = new ArrayList<>();
        for (int i = 0; i < DEFAULT_COMMODITIES.length; i++){
            Setting setting = new Setting();
            setting.setWidgetId(WIDGET_ID);
            setting.setQuoteType(QuoteType.GOODS);
            setting.setQuotePosition(i+1);
            setting.setQuoteSymbol(DEFAULT_COMMODITIES[i]);
            setting.setLastTradeDate(System.currentTimeMillis());

            settings.add(setting);
        }

        return settings;
    }

    private YahooMultiQueryData generateYahooMultiQueryData(List<Setting> settings){
        YahooMultiQueryData yahooMultiQueryData = new YahooMultiQueryData();
        List<YahooMultiQueryData.Quote> quotes = new ArrayList<>();
        yahooMultiQueryData.quotes = quotes;

        for (Setting setting: settings){
            YahooMultiQueryData.Quote quote = new YahooMultiQueryData.Quote();
            quote.symbol = setting.getQuoteSymbol();
            quote.LastTradePriceOnly = 100.0;
            quote.Change = 100.0;
            quote.ChangeinPercent = "0.8";
            quote.Name = "Name-" + setting.getQuoteSymbol();
            quote.Currency = "USD";

            quotes.add(quote);
        }

        return yahooMultiQueryData;
    }
}
