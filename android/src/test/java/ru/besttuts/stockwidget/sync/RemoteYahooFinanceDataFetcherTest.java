package ru.besttuts.stockwidget.sync;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.sync.model.YahooMultiQueryData;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 14.02.2015.
 */
public class RemoteYahooFinanceDataFetcherTest {

    private static final String TAG = makeLogTag(RemoteYahooFinanceDataFetcherTest.class);

    private static final String SYMBOL_CURRENCY_USDRUB = "USDRUB";
    private static final String SYMBOL_BRENT_OIL = "BZZ16.NYM";
    private static final String SYMBOL_CRUDE_OIL = "CLX16.NYM";

    private String[] exchange = new String[]{"EURUSD", "USDRUB", "EURRUB", "CNYRUB"};
    private String[] goods = new String[]{"GCF15.CMX", "PLF15.NYM", "PAF15.NYM", "SIF15.CMX", "HGF15.CMX"};

    RemoteYahooFinanceDataFetcher dataFetcher;

    @Before
    public void setUp() throws Exception {
        dataFetcher = new RemoteYahooFinanceDataFetcher();

    }

    @Ignore
    @Test
    public void testBuildYahooFinanceMultiQueryUrl() throws Exception {
        String expected = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20query.multi%20WHERE%20queries%3D%22select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('USDRUB'%2C'CNYRUB'%2C'EURRUB'%2C'EURUSD');select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20('SIF15.CMX'%2C'PAF15.NYM'%2C'PLF15.NYM'%2C'GCF15.CMX'%2C'HGF15.CMX')%3B%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

        for (String symbol : exchange) {
            dataFetcher.populateQuoteSet(QuoteType.CURRENCY, symbol);
        }
        for (String symbol : goods) {
            dataFetcher.populateQuoteSet(QuoteType.GOODS, symbol);
        }

        String actual = dataFetcher.buildYahooFinanceMultiQueryUrl();

        assertEquals(expected, actual);

    }

    @Ignore
    @Test
    public void testDownloadUrl() throws Exception {
        String sUrl = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20query.multi%20WHERE%20queries%3D%22select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('USDRUB'%2C'CNYRUB'%2C'EURRUB'%2C'EURUSD');select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20('SIF15.CMX'%2C'PAF15.NYM'%2C'PLF15.NYM'%2C'GCF15.CMX'%2C'HGF15.CMX')%3B%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

        assertNotNull(dataFetcher.downloadUrl(sUrl));

    }

    @Ignore
    @Test
    public void testDownloadQuotes() throws Exception {
        assertNotNull(dataFetcher.downloadQuotes());

    }

    @Test
    public void testGetYahooFinanceXchangeQuery() throws Exception {
        List<Setting> settings = new ArrayList<>();
        settings.add(createSetting(QuoteType.CURRENCY, SYMBOL_CURRENCY_USDRUB));

        dataFetcher.populateQuoteSet(settings);

        String expectedXchange = "select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('USDRUB')";
        assertEquals(expectedXchange, dataFetcher.getYahooFinanceXchangeQuery());
    }

    @Ignore
    @Test
    public void testGetYahooMultiQueryData() throws Exception {
        List<Setting> settings = new ArrayList<>();
        settings.add(createSetting(QuoteType.CURRENCY, SYMBOL_CURRENCY_USDRUB));
        settings.add(createSetting(QuoteType.GOODS, SYMBOL_BRENT_OIL));
        settings.add(createSetting(QuoteType.GOODS, SYMBOL_CRUDE_OIL));

        dataFetcher.populateQuoteSet(settings);

        YahooMultiQueryData yahooMultiQueryData = dataFetcher.getYahooMultiQueryData();

        assertEquals(2, yahooMultiQueryData.count);
        assertEquals(1, yahooMultiQueryData.rates.size());
        assertEquals(2, yahooMultiQueryData.quotes.size());

        assertEquals(SYMBOL_CURRENCY_USDRUB, yahooMultiQueryData.rates.get(0).id);
    }

    private static Setting createSetting(int quoteType, String quoteSymbol){
        Setting setting = new Setting();
        setting.setQuoteType(quoteType);
        setting.setQuoteSymbol(quoteSymbol);

        return setting;
    }

}
