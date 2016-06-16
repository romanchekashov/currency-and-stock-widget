package ru.besttuts.stockwidget;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.test.ProviderTestCase2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.QuoteContract.*;
import ru.besttuts.stockwidget.provider.QuoteProvider;

import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.LOGI;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author roman
 * @since 07.05.2016
 */
public class QuoteProviderTestCase extends ProviderTestCase2<QuoteProvider> {
    private static final String LOG_TAG = makeLogTag(QuoteProviderTestCase.class);

    private static final int WIDGET_ID = 1;

    private ContentResolver contentResolver;

    public QuoteProviderTestCase() {
        super(QuoteProvider.class, QuoteContract.CONTENT_AUTHORITY);
    }

    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
        contentResolver = getMockContentResolver();
    }

    public void testShouldCreateAndRetrieveSetting(){
        createSetting();
        try {
            Cursor cursor = contentResolver.query(Settings.CONTENT_URI, null, null, null, null);
            assertEquals(10, cursor.getCount());
            LOGI(LOG_TAG, "I: cursor.getCount: " + cursor.getCount());

            cursor.moveToFirst();
            do {
                Setting setting = transformCursorToSetting(cursor);
                LOGI(LOG_TAG, setting.toString());
            } while (cursor.moveToNext());
        } catch (Exception ex) {
            LOGE(LOG_TAG, "Error: " + ex.getClass() + ", " + ex.getMessage());
        }
    }

    public void testShouldCreateAndRetrieveTest(){
        fail();
    }

    private ContentProviderResult[] createSetting() {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        for (int i = 0; i < 10; i++){
            ContentValues values = new ContentValues();
            values.put(Settings.SETTING_ID, WIDGET_ID + "_" + i);
            values.put(Settings.SETTING_WIDGET_ID, WIDGET_ID);
            values.put(Settings.SETTING_QUOTE_POSITION, i);
            values.put(Settings.SETTING_QUOTE_TYPE, QuoteType.GOODS);
            values.put(Settings.SETTING_QUOTE_SYMBOL, String.format("BZZ1%d.NYM", i));
            values.put(Settings.LAST_TRADE_DATE, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());

            ops.add(ContentProviderOperation.newInsert(Settings.CONTENT_URI)
                    .withValues(values)
                    .withYieldAllowed(true)
                    .build());
        }

        try {
            return contentResolver.applyBatch(QuoteContract.CONTENT_AUTHORITY, ops);
        } catch (RemoteException e) {
            // do s.th.
        } catch (OperationApplicationException e) {
            // do s.th.
        }

        return null;
    }

    private static Setting transformCursorToSetting(Cursor cursor) {
        Setting setting = new Setting();
        setting.setId(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_ID)));
        setting.setWidgetId(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_WIDGET_ID)));
        setting.setQuotePosition(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_POSITION)));
        setting.setQuoteType(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_TYPE)));
        setting.setQuoteSymbol(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL)));
        setting.setLastTradeDate(cursor.getLong(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.LAST_TRADE_DATE)));

        return setting;
    }

    private static Model transformCursorToModel(Cursor cursor) {
        Model model = new Model();
        model.setId(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_ID)));
        model.setName(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_NAME)));
        model.setRate(cursor.getDouble(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_RATE)));
        model.setChange(cursor.getDouble(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_CHANGE)));
        model.setPercentChange(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE)));
        model.setQuoteType(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_TYPE)));
        try {
            model.setCurrency(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_CURRENCY)));
        } catch (IllegalArgumentException e) {
            // колонка 'model_currency' не существует у Валюты
        }

        return model;
    }
}
