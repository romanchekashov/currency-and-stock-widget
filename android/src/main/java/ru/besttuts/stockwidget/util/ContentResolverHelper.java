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
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.QuoteContract.QuoteLastTradeDates;
import ru.besttuts.stockwidget.provider.QuoteContract.Settings;

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

        Cursor cursor = contentResolver.query(Settings.CONTENT_URI, new String[]{
                QuoteLastTradeDates.SYMBOL,
                QuoteLastTradeDates.LAST_TRADE_DATE
        }, "code = ?s and last_trade_date > ?s", new String[]{
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

    private long getTodayUtcDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }
}
