package ru.besttuts.stockwidget.provider.db;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import ru.besttuts.stockwidget.BuildConfig;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.provider.QuoteDatabaseHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author rchekashov
 *         created on 11.03.2017
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class DbBackendAdapterTest implements DbContract {

    private static final int[] WIDGET_IDS = new int[]{1, 2};
    private static final String[] DEFAULT_COMMODITIES = new String[]{"BZF16.NYM", "NGZ15.NYM", "GCX15.CMX"};

    DbBackend dbBackend;
    DbBackendAdapter dbBackendAdapter;

    @Before
    public void setUp() throws Exception {
        QuoteDatabaseHelper helper = new QuoteDatabaseHelper(RuntimeEnvironment.application);
        dbBackend = new DbBackend(helper);
        dbBackendAdapter = new DbBackendAdapter(dbBackend);
    }

    @Test
    public void addSettingsRec_shouldSaveSettings(){
        dbBackendAdapter.addSettingsRec(WIDGET_IDS[0], 1, QuoteType.CURRENCY,
                new String[]{"EURUSD", "USDRUB", "EURRUB"});
        dbBackendAdapter.addSettingsRec(WIDGET_IDS[0], 4, QuoteType.GOODS, DEFAULT_COMMODITIES);

        List<Setting> settings = dbBackendAdapter.getSettingsByWidgetId(WIDGET_IDS[0]);
        assertEquals(6, settings.size());
    }
}
