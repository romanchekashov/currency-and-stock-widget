package ru.besttuts.stockwidget.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.besttuts.stockwidget.BuildConfig;

/**
 * @author rchekashov
 *         created on 19.03.2017
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class UtilsTest {

    @Test
    public void getNYSEInfo_shouldReturnQuoteData(){
        //
    }
}
