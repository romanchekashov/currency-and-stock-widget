package ru.besttuts.stockwidget.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.io.model.Result;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteLastTradeDate;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.provider.QuoteContract.Settings;
import ru.besttuts.stockwidget.sync.MyFinanceWS;
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.LOGI;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 14.01.2015.
 */
public class QuoteDataSource {

    private static final String TAG = makeLogTag(QuoteDataSource.class);

    private final Context context;

    private SQLiteDatabase mDatabase;

    private QuoteDatabaseHelper mDbHelper;

    public QuoteDataSource(Context context) {
        this.context = context;
        mDbHelper = new QuoteDatabaseHelper(context);
        LOGD(TAG, "QuoteDataSource initialized: " + this);
    }

//    public void open() throws SQLException {
//        mDatabase = mDbHelper.getWritableDatabase();
//    }

    public void close() {
        if (null != mDbHelper) mDbHelper.close();
    }

    private long getTodayUtcDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }

    public void addSettingsRec(int mAppWidgetId, int widgetItemPosition,
                               int type, String[] symbols) {

        long today = getTodayUtcDate();

        for (int i = 0; i < symbols.length; i++) {
            String symbol = symbols[i];
            int position = widgetItemPosition + i;
            String id = mAppWidgetId + "_" + position;

            Setting setting = new Setting();
            setting.setId(id);
            setting.setWidgetId(mAppWidgetId);
            setting.setQuotePosition(widgetItemPosition);
            setting.setQuoteType(type);
            setting.setQuoteSymbol(symbol);

            if (QuoteType.GOODS == type){
                updateSettingWithNewSymbolAndLastTradeDate(setting, today);
            }

            persist(setting);
        }

    }

    public void persist(Setting setting) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

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

    @Deprecated
    public void addModelRec(int quoteType, String symbol) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(QuoteContract.ModelColumns.MODEL_ID, symbol);
        values.put(QuoteContract.ModelColumns.MODEL_NAME,
                Utils.getModelNameFromResourcesBySymbol(context, quoteType, symbol));
        values.put(QuoteContract.ModelColumns.MODEL_RATE, 0);
        values.put(QuoteContract.ModelColumns.MODEL_CHANGE, 0);
        values.put(QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE, "0.0%");

        // Which row to update, based on the ID
        String selection = QuoteContract.ModelColumns.MODEL_ID + " LIKE ?";
        String[] selectionArgs = { symbol };

        long count = db.insertWithOnConflict(
                QuoteDatabaseHelper.Tables.MODELS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);

        LOGD(TAG, "insertWithOnConflict rows count = " + count);
    }

    public void addQuoteRec(Result result) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(QuoteContract.QuoteColumns.QUOTE_SYMBOL, result.symbol);
        values.put(QuoteContract.QuoteColumns.QUOTE_NAME, result.name);
        values.put(QuoteContract.QuoteColumns.QUOTE_TYPE, QuoteType.QUOTES);

        // Which row to update, based on the ID
        String selection = QuoteContract.QuoteColumns.QUOTE_SYMBOL + " LIKE ?";
        String[] selectionArgs = { result.symbol };

        long count = db.insert(
                QuoteDatabaseHelper.Tables.QUOTES,
                null,
                values);
        LOGD(TAG, "addQuoteRec: insert rows count = " + count);

        if (-1 == count) {
            throw new IllegalArgumentException(result.symbol + " " +
                    context.getString(R.string.exc_already_exists));
        }
    }

    public void addModelRec(Model model) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (null == model) {
            LOGE(TAG, "Model is NULL");
            return;
        }

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(QuoteContract.ModelColumns.MODEL_ID, model.getId());
        values.put(QuoteContract.ModelColumns.MODEL_NAME, model.getName());
        values.put(QuoteContract.ModelColumns.MODEL_RATE, model.getRate());
        values.put(QuoteContract.ModelColumns.MODEL_CHANGE, model.getChange());
        values.put(QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE, model.getPercentChange());
        values.put(QuoteContract.ModelColumns.MODEL_CURRENCY, model.getCurrency());

        // Which row to update, based on the ID
        String selection = QuoteContract.ModelColumns.MODEL_ID + " LIKE ?";
        String[] selectionArgs = { model.getId() };

//        if (null == mDatabase || !mDatabase.isOpen()) return;

        long count = db.insertWithOnConflict(
                QuoteDatabaseHelper.Tables.MODELS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);

        LOGD(TAG, "insertWithOnConflict rows count = " + count);
    }

    public Cursor getCursorSettingsByWidgetId(int widgetId) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                QuoteContract.SettingColumns.SETTING_ID,
                QuoteContract.SettingColumns.SETTING_WIDGET_ID,
                QuoteContract.SettingColumns.SETTING_QUOTE_POSITION,
                QuoteContract.SettingColumns.SETTING_QUOTE_TYPE,
                QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = QuoteContract.SettingColumns.SETTING_QUOTE_POSITION + " ASC";

        return db.query(
                QuoteDatabaseHelper.Tables.SETTINGS,  // The table to query
                projection,                               // The columns to return
                QuoteContract.SettingColumns.SETTING_WIDGET_ID + " = " + widgetId, // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
    }

    /**
     * Получаем Cursor настроек вместе с данными котировки, если они есть
     * Пример использования INNER JOIN
     *
     * @param widgetId идентификатор виджета
     * @return Cursor
     */
    public Cursor getCursorSettingsWithModelByWidgetId(int widgetId) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sqlQuery = "select * "
                + "from "+QuoteDatabaseHelper.Tables.SETTINGS+" as s "
                + "left join "+QuoteDatabaseHelper.Tables.MODELS+" as m "
                + "on s.setting_quote_symbol = m.model_id "
                + "where s.setting_widget_id = ? order by "
                + QuoteContract.SettingColumns.SETTING_QUOTE_POSITION + " asc;";

        return db.rawQuery(sqlQuery, new String[]{String.valueOf(widgetId)});

    }

    public Cursor getCursorSettingsWithoutModelByWidgetId(int widgetId) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sqlQuery = "select * "
                + "from "+QuoteDatabaseHelper.Tables.SETTINGS+" as s "
                + "left join "+QuoteDatabaseHelper.Tables.MODELS+" as m "
                + "on s.setting_quote_symbol = m.model_id "
                + "where s.setting_widget_id = ? and m.model_id is null order by "
                + QuoteContract.SettingColumns.SETTING_QUOTE_POSITION + " asc";

        return db.rawQuery(sqlQuery, new String[]{String.valueOf(widgetId)});

    }

    public Cursor getCursorModelsByWidgetId(int widgetId) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sqlQuery = "select m._id, m.model_id, m.model_name, "
                + "m.model_rate, m.model_change, m.model_percent_change, s.setting_quote_type "
                + "from "+QuoteDatabaseHelper.Tables.SETTINGS+" as s "
                + "inner join "+QuoteDatabaseHelper.Tables.MODELS+" as m "
                + "on s.setting_quote_symbol = m.model_id "
                + "where s.setting_widget_id = ? order by "
                + QuoteContract.SettingColumns.SETTING_QUOTE_POSITION + " asc";

        return db.rawQuery(sqlQuery, new String[]{String.valueOf(widgetId)});

    }

    public Cursor getCursorAllSettings() {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
                QuoteDatabaseHelper.Tables.SETTINGS,  // The table to query
                projection,                               // The columns to return
                null, // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
    }

    public void deleteSettingsByWidgetId(int widgetId) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int delCount = db.delete(QuoteDatabaseHelper.Tables.SETTINGS,
                QuoteContract.SettingColumns.SETTING_WIDGET_ID + " = " + widgetId, null);

        LOGD(TAG, "deleteSettingsByWidgetId: deleted rows count = " + delCount);

        // Если все записи Setting удалены, то удаляем все записи Model
        if (0 == db.rawQuery("select _id from "+ QuoteDatabaseHelper.Tables.SETTINGS, null).getCount()) {
            db.delete(QuoteDatabaseHelper.Tables.MODELS, null, null);
        }
    }

    public void deleteSettingsById(String settingId) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int delCount = db.delete(QuoteDatabaseHelper.Tables.SETTINGS,
                QuoteContract.SettingColumns.SETTING_ID + " = '" + settingId + "'", null);
        LOGD(TAG, "deleteSettingsById: deleted rows count = " + delCount);
    }

    public void deleteQuotesByIds(String[] symbols) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        for (String s: symbols) {
            db.delete(QuoteDatabaseHelper.Tables.QUOTES,
                    QuoteContract.QuoteColumns.QUOTE_SYMBOL + " = '" + s + "'", null);
        }
    }

    public void deleteSettingsByIdAndUpdatePositions(String settingId, int position) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        LOGD(TAG, String.format("deleteSettingsById: settingId = %s, position = %d",
                settingId, position));

        deleteSettingsById(settingId);

        Cursor cursor = db.rawQuery("select * from " + QuoteDatabaseHelper.Tables.SETTINGS
                + " where setting_quote_position > ?"
                + " order by setting_quote_position asc", new String[]{String.valueOf(position)});
        if (null == cursor || 0 == cursor.getCount()) return;

        cursor.moveToFirst();
        do {
            String widgetId = cursor.getString(cursor.getColumnIndexOrThrow(
                    QuoteContract.SettingColumns.SETTING_WIDGET_ID));
            String id = cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_ID));

            ContentValues args = new ContentValues();
            args.put(QuoteContract.SettingColumns.SETTING_ID, widgetId+"_"+position);
            args.put(QuoteContract.SettingColumns.SETTING_QUOTE_POSITION, position);

            db.update(QuoteDatabaseHelper.Tables.SETTINGS, args,
                    QuoteContract.SettingColumns.SETTING_ID + " = '" + id + "'", null);

//            mDatabase.rawQuery("update " + QuoteDatabaseHelper.Tables.SETTINGS
//                    + " set setting_quote_position = ?"
//                    + " where setting_id = ?", new String[]{String.valueOf(position), id});
            position++;
        } while (cursor.moveToNext());

        cursor.close();

        LOGD(TAG, "deleteSettingsByIdAndUpdatePositions");
    }

    public void deleteAll() {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(QuoteDatabaseHelper.Tables.SETTINGS, null, null);
        db.delete(QuoteDatabaseHelper.Tables.MODELS, null, null);
    }

    public Cursor getCursorMyQuotes() {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                QuoteContract.QuoteColumns.QUOTE_SYMBOL,
                QuoteContract.QuoteColumns.QUOTE_NAME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = BaseColumns._ID + " ASC";

        return db.query(
                QuoteDatabaseHelper.Tables.QUOTES,  // The table to query
                projection,                               // The columns to return
                null, // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
    }

    public Cursor getQuoteCursor(int quoteType) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                QuoteContract.QuoteColumns.QUOTE_SYMBOL,
                QuoteContract.QuoteColumns.QUOTE_NAME,
                QuoteContract.QuoteColumns.QUOTE_TYPE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = BaseColumns._ID + " ASC";
        String where = String.format("%s = %d", QuoteContract.QuoteColumns.QUOTE_TYPE, quoteType);

        return db.query(
                QuoteDatabaseHelper.Tables.QUOTES,  // The table to query
                projection,                               // The columns to return
                where, // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
    }

    // Методы для получения бизнес объектов
    public List<Setting> getSettingsByWidgetId(int widgetId) {
        Cursor cursor = getCursorSettingsByWidgetId(widgetId);
        if (0 >= cursor.getCount()) return new ArrayList();

        List settings = new ArrayList(cursor.getCount());
        cursor.moveToFirst();
        do {
            Setting setting = transformCursorToSetting(cursor);
            settings.add(setting);
        } while (cursor.moveToNext());

        cursor.close();

        return settings;
    }

    public List<Setting> getAllSettings() {
        Cursor cursor = getCursorAllSettings();
        if (0 >= cursor.getCount()) return new ArrayList();

        List settings = new ArrayList(cursor.getCount());
        cursor.moveToFirst();
        do {
            Setting setting = transformCursorToSetting(cursor);
            settings.add(setting);
            LOGD(TAG, "getAllSettings: " + setting.toString());
        } while (cursor.moveToNext());

        cursor.close();

        return settings;
    }

    public List<Setting> getAllSettingsWithCheck(){
        List<Setting> settings = getAllSettings();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long today = calendar.getTimeInMillis();

        for (Setting setting: settings){
            if (QuoteType.GOODS != setting.getQuoteType() || today < setting.getLastTradeDate()){
                continue;
            }

            updateSettingWithNewSymbolAndLastTradeDate(setting, today);

            persist(setting);
        }

        return settings;
    }

    private void updateSettingWithNewSymbolAndLastTradeDate(Setting setting, long today) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String symbol = setting.getQuoteSymbol();
        String code = symbol.substring(0, symbol.length() - 7);

        Cursor cursor = db.rawQuery("select * from " + QuoteDatabaseHelper.Tables.QUOTE_LAST_TRADE_DATES
                + " where code = ? and last_trade_date > ?"
                + " order by last_trade_date asc",
                new String[]{code, String.valueOf(today)});
        if (null == cursor || 0 == cursor.getCount()){
            try {
                MyFinanceWS ws = new MyFinanceWS(context);
                List<QuoteLastTradeDate> quoteLastTradeDates = ws.getQuotesWithLastTradeDate();

                insertQuoteLastTradeDate(quoteLastTradeDates);

                cursor = db.rawQuery("select * from " + QuoteDatabaseHelper.Tables.QUOTE_LAST_TRADE_DATES
                                + " where code = ? and last_trade_date > ?"
                                + " order by last_trade_date asc",
                        new String[]{code, String.valueOf(today)});

                if (null == cursor || 0 == cursor.getCount()) return;
            } catch (IOException e) {
                LOGE(TAG, e.getMessage());
                return;
            }
        }

        cursor.moveToFirst();

        String newSymbol = cursor.getString(cursor.getColumnIndexOrThrow(
                QuoteContract.QuoteLastTradeDateColumns.SYMBOL));
        long newLastTradeDate = cursor.getLong(cursor.getColumnIndexOrThrow(
                QuoteContract.QuoteLastTradeDateColumns.LAST_TRADE_DATE));

        setting.setQuoteSymbol(newSymbol);
        setting.setLastTradeDate(newLastTradeDate);

        cursor.close();
    }

    private void insertQuoteLastTradeDate(List<QuoteLastTradeDate> quoteLastTradeDates) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        for (QuoteLastTradeDate quote: quoteLastTradeDates){
            ContentValues values = new ContentValues();
            values.put(QuoteContract.QuoteLastTradeDateColumns.SYMBOL, quote.getSymbol());
            values.put(QuoteContract.QuoteLastTradeDateColumns.CODE, quote.getCode());
            values.put(QuoteContract.QuoteLastTradeDateColumns.LAST_TRADE_DATE, quote.getLastTradeDate());

            db.insertWithOnConflict(QuoteDatabaseHelper.Tables.QUOTE_LAST_TRADE_DATES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public List<Model> getModelsByWidgetId(int widgetId) {
        Cursor cursor = getCursorModelsByWidgetId(widgetId);
        if (0 >= cursor.getCount()){
            return new ArrayList<Model>();
        }

        List<Model> list = new ArrayList<Model>(cursor.getCount());
        cursor.moveToFirst();
        do {
            Model model = transformCursorToModel(cursor);
            list.add(model);
        } while (cursor.moveToNext());

        cursor.close();

        return list;
    }

    public static void printSetting(Cursor cursor){
        if (0 == cursor.getCount()) return;

        cursor.moveToFirst();
        do {
            Setting setting = transformCursorToSetting(cursor);
            LOGI(TAG, setting.toString());
        } while (cursor.moveToNext());
    }

    public static Setting transformCursorToSetting(Cursor cursor) {
        Setting setting = new Setting();
        setting.set_id(cursor.getInt(cursor.getColumnIndexOrThrow(Settings._ID)));
        setting.setId(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_ID)));
        setting.setWidgetId(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_WIDGET_ID)));
        setting.setQuotePosition(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_POSITION)));
        setting.setQuoteType(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_TYPE)));
        setting.setQuoteSymbol(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL)));
        setting.setLastTradeDate(cursor.getLong(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.LAST_TRADE_DATE)));

        return setting;
    }

    public static Model transformCursorToModel(Cursor cursor) {
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
