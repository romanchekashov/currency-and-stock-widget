package ru.besttuts.stockwidget.robolectric;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 14.02.2015.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RemoteYahooFinanceDataFetcherTest {

    private static final String TAG = makeLogTag(RemoteYahooFinanceDataFetcherTest.class);

    private static void println(String s) {
        System.out.println(TAG + ": " + s);
    }

    private String[] exchange = new String[]{"EURUSD", "USDRUB", "EURRUB", "CNYRUB"};
    private String[] goods = new String[]{"GCF15.CMX", "PLF15.NYM", "PAF15.NYM", "SIF15.CMX", "HGF15.CMX"};

    @Test
    public void testBuildYahooFinanceMultiQueryUrl() throws Exception {
        RemoteYahooFinanceDataFetcher dataFetcher = new RemoteYahooFinanceDataFetcher();

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

}
