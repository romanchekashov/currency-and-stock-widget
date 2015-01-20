package ru.besttuts.stockwidget.robolectric;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGI;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

import ru.besttuts.stockwidget.io.HandleJSON;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;

/**
 * Created by roman on 16.01.2015.
 */
@Config(manifest = "./android/src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class EconomicWidgetConfigureActivityTest {

    private static final String TAG = makeLogTag(EconomicWidgetConfigureActivityTest.class);

    private static void println(String s) {
        System.out.println(TAG + ": " + s);
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDataFetcher() throws Exception {

        String currencyUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('EURUSD'%2C'USDRUB'%2C'EURRUB'%2C'CNYRUB')%3B&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        RemoteYahooFinanceDataFetcher dataFetcher = new RemoteYahooFinanceDataFetcher();
        String data = dataFetcher.downloadUrl(currencyUrl);

        assertNotNull(data);

    }

    @Test
    public void testGenerateUrl() throws Exception {

        String[] currencies = new String[]{"RUB", "USD", "EUR", "AFN", "ALL", "DZD", "AOA", "XCD", "AMD", "BBD"};
        String[] goods = new String[]{"GCF15.CMX", "SIF15.CMX", "PLF15.NYM", "PAF15.NYM", "HGF15.CMX"};
        int[] allWidgetIds = new int[]{1, 2};

        List<Setting> settings = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            Setting setting = new Setting();
            setting.setId("1_"+i);
            setting.setWidgetId(1);
            setting.setQuotePosition(i);
            setting.setQuoteType(QuoteType.CURRENCY_EXCHANGE);
            setting.setQuoteSymbol(currencies[i]+currencies[9-i]);
            settings.add(setting);
        }

        for(int i = 0; i < 5; i++) {
            Setting setting = new Setting();
            setting.setId("2_"+i);
            setting.setWidgetId(2);
            setting.setQuotePosition(i);
            setting.setQuoteType(QuoteType.GOODS);
            setting.setQuoteSymbol(goods[i]);
            settings.add(setting);
        }

//        String currencyUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('EURUSD'%2C'USDRUB'%2C'EURRUB'%2C'CNYRUB')%3B&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        RemoteYahooFinanceDataFetcher dataFetcher = new RemoteYahooFinanceDataFetcher();

        String multiUrlExample = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20query.multi%20WHERE%20queries%3D%22select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('EURUSD'%2C'USDRUB'%2C'EURRUB'%2C'CNYRUB')%3B%20select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20('GCF15.CMX'%2C'PLF15.NYM'%2C'PAF15.NYM'%2C'SIF15.CMX'%2C'HGF15.CMX')%3B%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

        dataFetcher.populateQuoteSet(settings);

        HandleJSON handleJSON = new HandleJSON();
        handleJSON.readAndParseJSON(dataFetcher.downloadQuotes());

        Map<String, Model> symbolModelMap = handleJSON.getSymbolModelMap();

        Map<Integer, List<Model>> map = new HashMap<>();

        for (Setting setting: settings) {
            int widgetId = setting.getWidgetId();
            if (!map.containsKey(widgetId)) {
                map.put(widgetId, new ArrayList<Model>());
            }
            map.get(widgetId).add(symbolModelMap.get(setting.getQuoteSymbol()));

            println(setting.toString());
        }

        for (Map.Entry<Integer, List<Model>> me: map.entrySet()) {
            println("widgetId = " + me.getKey());
            for (Model model: me.getValue()) {
                println(model.toString());
            }
        }

//        String data = dataFetcher.downloadUrl(currencyUrl);

//        assertNotNull(data);

    }

}
