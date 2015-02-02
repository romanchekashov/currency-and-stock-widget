package ru.besttuts.stockwidget;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;

import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.util.Utils;

import static org.junit.Assert.assertSame;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void testGetModelNameFromResourcesBySymbol() throws Exception {

        assertEquals("EUR/RUB", Utils.getModelNameFromResourcesBySymbol(getContext(),
                QuoteType.CURRENCY_EXCHANGE, "EURRUB"));
        assertEquals(getContext().getString(R.string.gcf15_cmx),
                Utils.getModelNameFromResourcesBySymbol(getContext(),
                        QuoteType.GOODS, "GCF15.CMX"));

    }
}