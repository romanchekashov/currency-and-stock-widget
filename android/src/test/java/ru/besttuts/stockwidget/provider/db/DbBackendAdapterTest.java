package ru.besttuts.stockwidget.provider.db;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.provider.AppDatabase;
import ru.besttuts.stockwidget.provider.model.QuoteType;
import ru.besttuts.stockwidget.provider.model.Setting;
import ru.besttuts.stockwidget.util.UtilsTest;

import static org.junit.Assert.assertEquals;

/**
 * @author rchekashov
 * created on 11.03.2017
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class DbBackendAdapterTest {

    private static final int[] WIDGET_IDS = new int[]{1, 2};
    private static final String[] DEFAULT_COMMODITIES = new String[]{"BZF16.NYM", "NGZ15.NYM", "GCX15.CMX"};

    AppDatabase database;
    DbBackendAdapter dbBackendAdapter;

    @Before
    public void setUp() throws Exception {
        database = AppDatabase.getInstance(RuntimeEnvironment.application);
        dbBackendAdapter = new DbBackendAdapterImpl(database);
    }

    @Test
    public void addSettingsRec_shouldSaveSettings() throws InterruptedException {
//        final List<Setting> settings = new ArrayList<>();
//        UtilsTest.runBackgroundSync(() -> {
//            dbBackendAdapter.addSettingsRec(WIDGET_IDS[0], 1, QuoteType.CURRENCY,
//                    new String[]{"EURUSD", "USDRUB", "EURRUB"});
//            dbBackendAdapter.addSettingsRec(WIDGET_IDS[0], 4, QuoteType.COMMODITY, DEFAULT_COMMODITIES);
//
//            settings.addAll(dbBackendAdapter.getSettingsByWidgetId(WIDGET_IDS[0]));
//        });
//        assertEquals(6, settings.size());
    }

//    @Test
//    public void deleteSettingsByIdAndUpdatePositions_shouldSaveSettings(){
//        dbBackendAdapter.addSettingsRec(WIDGET_IDS[0], 1, QuoteType.CURRENCY,
//                new String[]{"EURUSD", "USDRUB", "EURRUB"});
//        dbBackendAdapter.addSettingsRec(WIDGET_IDS[0], 4, QuoteType.COMMODITY, DEFAULT_COMMODITIES);
//
//        List<Setting> settings = dbBackendAdapter.getSettingsByWidgetId(WIDGET_IDS[0]);
//        assertEquals(6, settings.size());
//        for (int i = 1; i <= 6; i++){
//            assertEquals(i, settings.get(i-1).getQuotePosition());
//        }
//
//        Setting setting = settings.get(0);
//        dbBackend.deleteSettingsByIdAndUpdatePositions(setting.getId(), setting.getQuotePosition());
//
//        settings = dbBackendAdapter.getSettingsByWidgetId(WIDGET_IDS[0]);
//        assertEquals(5, settings.size());
//        for (int i = 1; i <= 5; i++){
//            assertEquals(i, settings.get(i-1).getQuotePosition());
//        }
//    }
}
