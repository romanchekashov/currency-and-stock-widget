package ru.besttuts.stockwidget.provider.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.annotation.VisibleForTesting;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ru.besttuts.stockwidget.model.QuoteLastTradeDate;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.QuoteDatabaseHelper;
import ru.besttuts.stockwidget.sync.MyFinanceWS;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 *         created on 1/24/2017.
 */

public class DbBackend implements DbContract {
    private static final String TAG = makeLogTag(DbBackend.class);

    private final QuoteDatabaseHelper mDbOpenHelper;
    private Context mContext;

    DbBackend(Context context) {
        mDbOpenHelper = new QuoteDatabaseHelper(context);
        mContext = context;
    }

    @VisibleForTesting
    DbBackend(QuoteDatabaseHelper dbOpenHelper) {
        mDbOpenHelper = dbOpenHelper;
    }

    Cursor getCursorModelsByWidgetId(int widgetId) {
        final SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String sqlQuery = "select m._id, m.model_id, m.model_name, "
                + "m.model_rate, m.model_change, m.model_percent_change, s.setting_quote_type "
                + "from "+QuoteDatabaseHelper.Tables.SETTINGS+" as s "
                + "inner join "+QuoteDatabaseHelper.Tables.MODELS+" as m "
                + "on s.setting_quote_symbol = m.model_id "
                + "where s.setting_widget_id = ? order by "
                + QuoteContract.SettingColumns.SETTING_QUOTE_POSITION + " asc";

        return db.rawQuery(sqlQuery, new String[]{String.valueOf(widgetId)});
    }

    Cursor getCursorSettingsWithModelByWidgetId(int widgetId) {
        final SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String sqlQuery = "select * "
                + "from "+QuoteDatabaseHelper.Tables.SETTINGS+" as s "
                + "left join "+QuoteDatabaseHelper.Tables.MODELS+" as m "
                + "on s.setting_quote_symbol = m.model_id "
                + "where s.setting_widget_id = ? order by "
                + QuoteContract.SettingColumns.SETTING_QUOTE_POSITION + " asc;";

        return db.rawQuery(sqlQuery, new String[]{String.valueOf(widgetId)});
    }

    public Cursor getCursorSettingsByWidgetId(int widgetId) {
        final SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                QuoteContract.SettingColumns.SETTING_ID,
                QuoteContract.SettingColumns.SETTING_WIDGET_ID,
                QuoteContract.SettingColumns.SETTING_QUOTE_POSITION,
                QuoteContract.SettingColumns.SETTING_QUOTE_TYPE,
                QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL,
                QuoteContract.SettingColumns.LAST_TRADE_DATE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = QuoteContract.SettingColumns.SETTING_QUOTE_POSITION + " ASC";

        return db.query(
                QuoteDatabaseHelper.Tables.SETTINGS,     // The table to query
                projection,                              // The columns to return
                SettingColumns.SETTING_WIDGET_ID + "=?", // The columns for the WHERE clause
                new String[]{String.valueOf(widgetId)},  // The values for the WHERE clause
                null,                                    // don't group the rows
                null,                                    // don't filter by row groups
                sortOrder                                // The sort order
        );
    }

    void insertQuoteLastTradeDate(List<QuoteLastTradeDate> quoteLastTradeDates) {
        final SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        for (QuoteLastTradeDate quote: quoteLastTradeDates){
            ContentValues values = new ContentValues();
            values.put(QuoteContract.QuoteLastTradeDateColumns.SYMBOL, quote.getSymbol());
            values.put(QuoteContract.QuoteLastTradeDateColumns.CODE, quote.getCode());
            values.put(QuoteContract.QuoteLastTradeDateColumns.LAST_TRADE_DATE, quote.getLastTradeDate());

            db.insertWithOnConflict(QuoteDatabaseHelper.Tables.QUOTE_LAST_TRADE_DATES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    Cursor getCursorQuoteLastTradeDateForCurrentDay(String code, long today){
        final SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        return db.rawQuery("select * from " + QuoteDatabaseHelper.Tables.QUOTE_LAST_TRADE_DATES
                        + " where code = ? and last_trade_date > ?"
                        + " order by last_trade_date asc",
                new String[]{code, String.valueOf(today)});
    }

    void persist(Setting setting) {
        final SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        if(null == setting.getId() || setting.getId().isEmpty()){
            setting.setId(setting.getWidgetId() + "_" + setting.getQuotePosition());
        }

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(QuoteContract.SettingColumns.SETTING_ID, setting.getId());
        values.put(QuoteContract.SettingColumns.SETTING_WIDGET_ID, setting.getWidgetId());
        values.put(QuoteContract.SettingColumns.SETTING_QUOTE_POSITION, setting.getQuotePosition());
        values.put(QuoteContract.SettingColumns.SETTING_QUOTE_TYPE, setting.getQuoteType());
        values.put(QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL, setting.getQuoteSymbol());
        values.put(QuoteContract.SettingColumns.LAST_TRADE_DATE, setting.getLastTradeDate());

        long rowId = db.insertWithOnConflict(
                QuoteDatabaseHelper.Tables.SETTINGS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);

        LOGD(TAG, "insertWithOnConflict id = " + rowId);
    }
}
