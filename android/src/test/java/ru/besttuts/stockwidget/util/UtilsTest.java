package ru.besttuts.stockwidget.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.besttuts.stockwidget.BuildConfig;

import static junit.framework.Assert.assertEquals;

/**
 * @author rchekashov
 *         created on 19.03.2017
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class UtilsTest {

    @Test
    public void encodeYahooApiQuery_shouldWork(){
        String expectedEncodedQuery = "SELECT%20*%20FROM%20query.multi%20WHERE%20queries%3D%22select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('EURUSD')%3B%0Aselect%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20('NGU17.NYM'%2C'BZV17.NYM'%2C'GCQ17.CMX')%22";
        String query = "SELECT * FROM query.multi WHERE queries=\"select * from yahoo.finance.xchange where pair in ('EURUSD');select * from yahoo.finance.quotes where symbol in ('NGU17.NYM','BZV17.NYM','GCQ17.CMX')\"";

        assertEquals(expectedEncodedQuery, Utils.encodeYahooApiQuery(query));
    }
}
