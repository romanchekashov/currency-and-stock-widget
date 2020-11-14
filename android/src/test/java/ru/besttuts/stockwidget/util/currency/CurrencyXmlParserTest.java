package ru.besttuts.stockwidget.util.currency;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import ru.besttuts.stockwidget.BuildConfig;

/**
 * @author rchekashov
 *         created on 28.08.2017
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class CurrencyXmlParserTest {
    private static final String PATH_TO_SAVE = "D:/androidWorkspace/currency-and-stock-widget/android/src/main/res/values/currency_new.xml";

    CurrencyXmlParser currencyXmlParser = new CurrencyXmlParser();

    @Ignore
    @Test
    public void loadXmlAnrWriteToFile_updateCurrencyList(){
        currencyXmlParser.loadXmlAnrWriteToFile(PATH_TO_SAVE);
    }

    @Ignore
    @Test
    public void loadYahooQuotes_load(){
        List<YahooQuote> quotes = currencyXmlParser.loadYahooQuotes();
        for (YahooQuote quote: quotes) System.out.println(quote.name);
    }
}
