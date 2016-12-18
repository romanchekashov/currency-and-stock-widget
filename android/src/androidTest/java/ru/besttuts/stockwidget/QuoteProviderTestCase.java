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
import ru.besttuts.stockwidget.model.QuoteLastTradeDate;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.QuoteContract.*;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.provider.QuoteProvider;
import ru.besttuts.stockwidget.util.ContentResolverHelper;
import ru.besttuts.stockwidget.provider.QuoteContract.QuoteLastTradeDates;


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
    private ContentResolverHelper contentResolverHelper;

    public QuoteProviderTestCase() {
        super(QuoteProvider.class, QuoteContract.CONTENT_AUTHORITY);
    }

    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
        contentResolver = getMockContentResolver();
        contentResolverHelper = new ContentResolverHelper(contentResolver);
    }

    public void testShouldCreateAndRetrieveSetting1(){
//        insertQuoteLastTradeDate();
        try {
            Cursor cursor = contentResolver.query(QuoteLastTradeDates.CONTENT_URI, null, null, null, null);
//            assertEquals(10, cursor.getCount());
            LOGI(LOG_TAG, "I: cursor.getCount: " + cursor.getCount());

            cursor.moveToFirst();
            do {
                QuoteLastTradeDate setting = transformQuoteLastTradeDate(cursor);
                LOGI(LOG_TAG, setting.toString());
            } while (cursor.moveToNext());
        } catch (Exception ex) {
            LOGE(LOG_TAG, "Error: " + ex.getClass() + ", " + ex.getMessage());
        }
    }

    public void testShouldCreateAndRetrieveSetting(){
        createSetting(1, 10);
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

    public void testShouldCreate4SettingDeleteSecondAndUpdatePositions(){
        createSetting(1, 4);
        try {
            Cursor cursor = contentResolver.query(Settings.CONTENT_URI, null, null, null, null);
            assertEquals(4, cursor.getCount());

            LOGI(LOG_TAG, "I: cursor.getCount: " + cursor.getCount());
            QuoteDataSource.printSetting(cursor);

            contentResolverHelper.deleteSettingWithOthersPositionUpdate(2, WIDGET_ID, 2);
//            contentResolver.delete(Settings.CONTENT_URI, Settings._ID + " = ?", new String[]{"2"});

            cursor = contentResolver.query(Settings.CONTENT_URI, null, null, null, null);
            assertEquals(3, cursor.getCount());

            LOGI(LOG_TAG, "I: cursor.getCount: " + cursor.getCount());
            QuoteDataSource.printSetting(cursor);
        } catch (Exception ex) {
            LOGE(LOG_TAG, "Error: " + ex.getClass() + ", " + ex.getMessage());
        }
    }

    public void testShouldDeleteAllSettingsAndModels(){
        createSetting(1, 4);
        try {
            Cursor cursor = contentResolver.query(Settings.CONTENT_URI, null, null, null, null);
            assertEquals(4, cursor.getCount());

            LOGI(LOG_TAG, "I: cursor.getCount: " + cursor.getCount());
            QuoteDataSource.printSetting(cursor);

            contentResolverHelper.deleteAll();

            cursor = contentResolver.query(Settings.CONTENT_URI, null, null, null, null);
            assertEquals(0, cursor.getCount());

            LOGI(LOG_TAG, "I: cursor.getCount: " + cursor.getCount());
            QuoteDataSource.printSetting(cursor);
        } catch (Exception ex) {
            LOGE(LOG_TAG, "Error: " + ex.getClass() + ", " + ex.getMessage());
        }
    }

//    public void testShouldCreateAndRetrieveTest(){
//        fail();
//    }

    private void createSetting(int firstPos, int len) {
        List<String> symbols = new ArrayList<>();
        for (int i = firstPos; i <= len; i++){
            symbols.add("TEST.SYMBOL"+i);
        }
        contentResolverHelper.addSettingsRec(WIDGET_ID, firstPos, QuoteType.GOODS, symbols.toArray(new String[len]));
    }

    private static QuoteLastTradeDate transformQuoteLastTradeDate(Cursor cursor) {
        QuoteLastTradeDate setting = new QuoteLastTradeDate();
        setting.set_id(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteLastTradeDates._ID)));
        setting.setCode(cursor.getString(cursor.getColumnIndexOrThrow(QuoteLastTradeDates.CODE)));
        setting.setSymbol(cursor.getString(cursor.getColumnIndexOrThrow(QuoteLastTradeDates.SYMBOL)));
        setting.setLastTradeDate(cursor.getLong(cursor.getColumnIndexOrThrow(QuoteLastTradeDates.LAST_TRADE_DATE)));

        return setting;
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

    private void insertQuoteLastTradeDate(){
        contentResolverHelper.addQuoteLastTradeDate("BZ", "BZZ15.NYM", 1447372800000L);
        contentResolverHelper.addQuoteLastTradeDate("BZ", "BZJ15.NYM", 1426464000000L);
        contentResolverHelper.addQuoteLastTradeDate("BZ", "BZF16.NYM", 1450224000000L);
        contentResolverHelper.addQuoteLastTradeDate("BZ", "BZH16.NYM", 1454025600000L);
        contentResolverHelper.addQuoteLastTradeDate("BZ", "BZJ16.NYM", 1456704000000L);
        contentResolverHelper.addQuoteLastTradeDate("BZ", "BZK16.NYM", 1459382400000L);
        contentResolverHelper.addQuoteLastTradeDate("BZ", "BZM16.NYM", 1461888000000L);
        contentResolverHelper.addQuoteLastTradeDate("BZ", "BZN16.NYM", 1464652800000L);
        contentResolverHelper.addQuoteLastTradeDate("BZ", "BZQ16.NYM", 1467244800000L);
        contentResolverHelper.addQuoteLastTradeDate("BZ", "BZU16.NYM", 1469750400000L); // July

        contentResolverHelper.addQuoteLastTradeDate("CL", "CLX15.NYM", 1445299200000L);
        contentResolverHelper.addQuoteLastTradeDate("CL", "CLZ15.NYM", 1447977600000L);
        contentResolverHelper.addQuoteLastTradeDate("CL", "CLH16.NYM", 1456099200000L);
        contentResolverHelper.addQuoteLastTradeDate("CL", "CLJ16.NYM", 1458518400000L);
        contentResolverHelper.addQuoteLastTradeDate("CL", "CLK16.NYM", 1461110400000L);
        contentResolverHelper.addQuoteLastTradeDate("CL", "CLM16.NYM", 1463702400000L);
        contentResolverHelper.addQuoteLastTradeDate("CL", "CLN16.NYM", 1466467200000L);
        contentResolverHelper.addQuoteLastTradeDate("CL", "CLQ16.NYM", 1468972800000L); // July

        contentResolverHelper.addQuoteLastTradeDate("GC", "GCV15.CMX", 1445990400000L);
        contentResolverHelper.addQuoteLastTradeDate("GC", "GCX15.CMX", 1448409600000L);
        contentResolverHelper.addQuoteLastTradeDate("GC", "GCF16.CMX", 1453852800000L);
        contentResolverHelper.addQuoteLastTradeDate("GC", "GCG16.CMX", 1456358400000L);
        contentResolverHelper.addQuoteLastTradeDate("GC", "GCH16.CMX", 1459209600000L);
        contentResolverHelper.addQuoteLastTradeDate("GC", "GCJ16.CMX", 1461715200000L); // Apr

        contentResolverHelper.addQuoteLastTradeDate("SI", "SIZ15.CMX", 1451347200000L);
        contentResolverHelper.addQuoteLastTradeDate("SI", "SIF16.CMX", 1453852800000L);
        contentResolverHelper.addQuoteLastTradeDate("SI", "SIG16.CMX", 1456358400000L);
        contentResolverHelper.addQuoteLastTradeDate("SI", "SIH16.CMX", 1459209600000L); // Mar

        contentResolverHelper.addQuoteLastTradeDate("PL", "PLF16.NYM", 1453852800000L);
        contentResolverHelper.addQuoteLastTradeDate("PL", "PLG16.NYM", 1456358400000L);
        contentResolverHelper.addQuoteLastTradeDate("PL", "PLH16.NYM", 1459209600000L); // Mar

        contentResolverHelper.addQuoteLastTradeDate("PA", "PAF16.NYM", 1453852800000L);
        contentResolverHelper.addQuoteLastTradeDate("PA", "PAG16.NYM", 1456358400000L);
        contentResolverHelper.addQuoteLastTradeDate("PA", "PAH16.NYM", 1459209600000L); // Mar

        contentResolverHelper.addQuoteLastTradeDate("HG", "HGF16.CMX", 1453852800000L);
        contentResolverHelper.addQuoteLastTradeDate("HG", "HGG16.CMX", 1456358400000L);
        contentResolverHelper.addQuoteLastTradeDate("HG", "HGH16.CMX", 1459209600000L); // Mar

        contentResolverHelper.addQuoteLastTradeDate("NG", "NGG16.NYM", 1453852800000L);
        contentResolverHelper.addQuoteLastTradeDate("NG", "NGH16.NYM", 1456358400000L);
        contentResolverHelper.addQuoteLastTradeDate("NG", "NGJ16.NYM", 1459209600000L); // Mar

        contentResolverHelper.addQuoteLastTradeDate("C", "CZ15.CBT", 1450051200000L);
        contentResolverHelper.addQuoteLastTradeDate("C", "CH16.CBT", 1457913600000L);
        contentResolverHelper.addQuoteLastTradeDate("C", "CK16.CBT", 1463097600000L); // May

        contentResolverHelper.addQuoteLastTradeDate("S", "SF16.CBT", 1452729600000L);
        contentResolverHelper.addQuoteLastTradeDate("S", "SH16.CBT", 1457913600000L);
        contentResolverHelper.addQuoteLastTradeDate("S", "SK16.CBT", 1463097600000L); // May

        contentResolverHelper.addQuoteLastTradeDate("ZW", "ZWZ15.CBT", 1450051200000L);
        contentResolverHelper.addQuoteLastTradeDate("ZW", "ZWH16.CBT", 1457913600000L);
        contentResolverHelper.addQuoteLastTradeDate("ZW", "ZWK16.CBT", 1463097600000L); // May

        contentResolverHelper.addQuoteLastTradeDate("CC", "CCH15.NYB", 1426464000000L);
        contentResolverHelper.addQuoteLastTradeDate("CC", "CCK16.NYB", 1463097600000L); // May

        contentResolverHelper.addQuoteLastTradeDate("KC", "KCH15.NYB", 1426723200000L);
        contentResolverHelper.addQuoteLastTradeDate("KC", "KCH16.NYB", 1458259200000L);
        contentResolverHelper.addQuoteLastTradeDate("KC", "KCK16.NYB", 1463529600000L); // May

        contentResolverHelper.addQuoteLastTradeDate("CT", "CTH15.NYB", 1425859200000L);
        contentResolverHelper.addQuoteLastTradeDate("CT", "CTH16.NYB", 1457395200000L);
        contentResolverHelper.addQuoteLastTradeDate("CT", "CTK16.NYB", 1462492800000L); // May

        contentResolverHelper.addQuoteLastTradeDate("LB", "LBH15.CME", 1426204800000L);
        contentResolverHelper.addQuoteLastTradeDate("LB", "LBH16.CME", 1458000000000L);
        contentResolverHelper.addQuoteLastTradeDate("LB", "LBK16.CME", 1463097600000L); // May

        contentResolverHelper.addQuoteLastTradeDate("OJ", "OJH15.NYB", 1426032000000L);
        contentResolverHelper.addQuoteLastTradeDate("OJ", "OJH16.NYB", 1457568000000L);
        contentResolverHelper.addQuoteLastTradeDate("OJ", "OJK16.NYB", 1462838400000L); // May

        contentResolverHelper.addQuoteLastTradeDate("SB", "SBH15.NYB", 1424995200000L);
        contentResolverHelper.addQuoteLastTradeDate("SB", "SBH16.NYB", 1456704000000L);
        contentResolverHelper.addQuoteLastTradeDate("SB", "SBK16.NYB", 1461888000000L); // Apr
    }
}
