package ru.besttuts.stockwidget.util;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.QuoteContract.QuoteLastTradeDates;
import ru.besttuts.stockwidget.provider.QuoteContract.Settings;

import static ru.besttuts.stockwidget.util.LogUtils.LOGI;

/**
 * @author rchekashov
 * created on 18.06.2016
 */
public class ContentResolverHelper {

    private ContentResolver contentResolver;

    public ContentResolverHelper(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void addSettingsRec(int widgetId, int widgetItemPos,
                               int type, String[] symbols) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int pos;
        for (String symbol: symbols){
            ContentValues values = new ContentValues();
            pos = widgetItemPos++;

            values.put(Settings.SETTING_ID, widgetId + "_" + pos);
            values.put(Settings.SETTING_WIDGET_ID, widgetId);
            values.put(Settings.SETTING_QUOTE_POSITION, pos);
            values.put(Settings.SETTING_QUOTE_TYPE, type);
            values.put(Settings.SETTING_QUOTE_SYMBOL, symbol);

            if (QuoteType.GOODS == type){
                updateSettingWithNewSymbolAndLastTradeDate(values);
            }

            ops.add(ContentProviderOperation.newInsert(Settings.CONTENT_URI)
                    .withValues(values)
                    .withYieldAllowed(true)
                    .build());
        }

        try {
            contentResolver.applyBatch(QuoteContract.CONTENT_AUTHORITY, ops);
        } catch (RemoteException e) {
            // do s.th.
        } catch (OperationApplicationException e) {
            // do s.th.
        }
    }

    public void addQuoteLastTradeDate(String code, String symbol, long lastTradeDate){
        ContentValues values = new ContentValues();

        values.put(QuoteLastTradeDates.CODE, code);
        values.put(QuoteLastTradeDates.SYMBOL, symbol);
        values.put(QuoteLastTradeDates.LAST_TRADE_DATE, lastTradeDate);

        contentResolver.insert(QuoteLastTradeDates.CONTENT_URI, values);
    }

    private void updateSettingWithNewSymbolAndLastTradeDate(ContentValues values) {

        String symbol = (String) values.get(Settings.SETTING_QUOTE_SYMBOL);
        String code = symbol.substring(0, symbol.length() - 7);

        Cursor cursor = contentResolver.query(QuoteLastTradeDates.CONTENT_URI, new String[]{
                QuoteLastTradeDates.SYMBOL,
                QuoteLastTradeDates.LAST_TRADE_DATE
        }, "code = ? and last_trade_date > ?", new String[]{
                code, String.valueOf(getTodayUtcDate())
        }, "last_trade_date asc");

        if (null == cursor || 0 == cursor.getCount()) return;

        cursor.moveToFirst();
        String newSymbol = cursor.getString(cursor.getColumnIndexOrThrow(
                QuoteLastTradeDates.SYMBOL));
        long newLastTradeDate = cursor.getLong(cursor.getColumnIndexOrThrow(
                QuoteLastTradeDates.LAST_TRADE_DATE));

        values.put(Settings.SETTING_QUOTE_SYMBOL, newSymbol);
        values.put(Settings.LAST_TRADE_DATE, newLastTradeDate);

        cursor.close();
    }

    public void deleteSettingWithOthersPositionUpdate(int _id, int widgetId, int currentPos) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newDelete(Settings.buildUri(String.valueOf(_id)))
                .withYieldAllowed(true)
                .build());

        Cursor cursor = contentResolver.query(Settings.CONTENT_URI, new String[]{
                        Settings._ID
        }, "setting_widget_id = ? and setting_quote_position > ?", new String[]{
                String.valueOf(widgetId), String.valueOf(currentPos)
        }, "setting_quote_position asc");

        int pos;
        cursor.moveToFirst();
        do {
            ContentValues values = new ContentValues();
            pos = currentPos++;

            values.put(Settings.SETTING_ID, widgetId + "_" + pos);
            values.put(Settings.SETTING_QUOTE_POSITION, pos);

            ops.add(ContentProviderOperation.newUpdate(Settings.CONTENT_URI)
                    .withSelection(Settings._ID + " = ?", new String[]{
                            cursor.getString(cursor.getColumnIndexOrThrow(Settings._ID))
                    })
                    .withValues(values)
                    .withYieldAllowed(true)
                    .build());
        } while (cursor.moveToNext());

        cursor.close();

        try {
            contentResolver.applyBatch(QuoteContract.CONTENT_AUTHORITY, ops);
        } catch (RemoteException e) {
            // do s.th.
        } catch (OperationApplicationException e) {
            // do s.th.
        }
    }

    private long getTodayUtcDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }

    public void deleteAll() {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newDelete(Settings.CONTENT_URI)
                .withYieldAllowed(true)
                .build());

        try {
            contentResolver.applyBatch(QuoteContract.CONTENT_AUTHORITY, ops);
        } catch (RemoteException e) {
            // do s.th.
        } catch (OperationApplicationException e) {
            // do s.th.
        }
    }
}
