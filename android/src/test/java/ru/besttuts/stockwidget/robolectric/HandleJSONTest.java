package ru.besttuts.stockwidget.robolectric;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;
import java.util.Map;

import ru.besttuts.stockwidget.io.HandleJSON;
import ru.besttuts.stockwidget.model.Currency;
import ru.besttuts.stockwidget.model.Model;
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
public class HandleJSONTest {

    private static final String TAG = makeLogTag(HandleJSONTest.class);

    private static void println(String s) {
        System.out.println(TAG + ": " + s);
    }

    private String[] exchange = new String[]{"EURUSD", "USDRUB", "EURRUB", "CNYRUB"};
    private String[] goods = new String[]{"GCF15.CMX", "PLF15.NYM", "PAF15.NYM", "SIF15.CMX", "HGF15.CMX"};

    @Test
    public void testReadAndParseJSON() throws Exception {

        RemoteYahooFinanceDataFetcher dataFetcher = new RemoteYahooFinanceDataFetcher();

        for (String symbol : exchange) {
            dataFetcher.populateQuoteSet(QuoteType.CURRENCY, symbol);
        }
        for (String symbol : goods) {
            dataFetcher.populateQuoteSet(QuoteType.GOODS, symbol);
        }

        HandleJSON handleJSON = new HandleJSON(null);

        handleJSON.readAndParseJSON(dataFetcher.downloadQuotes());

        Map<String, Model> symbolModelMap = handleJSON.getSymbolModelMap();

        assertEquals(exchange.length + goods.length, symbolModelMap.size());

    }

    @Test
    public void testReadCurrency() throws Exception {

        HandleJSON handleJSON = new HandleJSON(null);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1");
        jsonObject.put("Name", "EUR to USD");
        jsonObject.put("Rate", 1.2);
        jsonObject.put("Ask", 1.3);
        jsonObject.put("Bid", 1.2);

        Method method = HandleJSON.class.getDeclaredMethod("readCurrency", JSONObject.class);
        method.setAccessible(true);

        Currency currency = (Currency) method.invoke(handleJSON, jsonObject);

        assertNotNull(currency);

        assertEquals(jsonObject.get("id"), currency.getId());


    }

}
