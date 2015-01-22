package ru.besttuts.stockwidget.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;

/**
 * Created by roman on 14.01.2015.
 */
public class QuoteDataSource {

    private final static String LOG_TAG = "EconomicWidget.QuoteDataSource";

    private final Context context;

    private SQLiteDatabase mDatabase;

    private QuoteDatabaseHelper mDbHelper;

    public QuoteDataSource(Context context) {
        this.context = context;
    }

    public void open() throws SQLException {
        mDbHelper = new QuoteDatabaseHelper(context);
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        if (null != mDbHelper) mDbHelper.close();
    }

    public void addSettingsRec(int mAppWidgetId, int widgetItemPosition, String type, String symbol) {

        String id = mAppWidgetId + "_" + widgetItemPosition;

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(QuoteContract.SettingColumns.SETTING_ID, id);
        values.put(QuoteContract.SettingColumns.SETTING_WIDGET_ID, mAppWidgetId);
        values.put(QuoteContract.SettingColumns.SETTING_QUOTE_POSITION, widgetItemPosition);
        values.put(QuoteContract.SettingColumns.SETTING_QUOTE_TYPE, type);
        values.put(QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL, symbol);

        // Which row to update, based on the ID
        String selection = QuoteContract.SettingColumns.SETTING_ID + " LIKE ?";
        String[] selectionArgs = { id };

        long count = mDatabase.insertWithOnConflict(
                QuoteDatabaseHelper.Tables.SETTINGS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);

        Log.d(LOG_TAG, "insertWithOnConflict rows count = " + count);
    }

    public void addModelRec(int mAppWidgetId, int widgetItemPosition, Model model) {

        String id = mAppWidgetId + "_" + widgetItemPosition;

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(QuoteContract.ModelColumns.MODEL_ID, id);
        values.put(QuoteContract.ModelColumns.MODEL_WIDGET_ID, mAppWidgetId);
        values.put(QuoteContract.ModelColumns.MODEL_QUOTE_POSITION, widgetItemPosition);
        values.put(QuoteContract.ModelColumns.MODEL_NAME, model.getName());
        values.put(QuoteContract.ModelColumns.MODEL_RATE, model.getRate());
        values.put(QuoteContract.ModelColumns.MODEL_CHANGE, model.getChange());
        values.put(QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE, model.getPercentChange());

        // Which row to update, based on the ID
        String selection = QuoteContract.ModelColumns.MODEL_ID + " LIKE ?";
        String[] selectionArgs = { id };

        long count = mDatabase.insertWithOnConflict(
                QuoteDatabaseHelper.Tables.MODELS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);

        Log.d(LOG_TAG, "insertWithOnConflict rows count = " + count);
    }

    public Cursor getCursorSettingsByWidgetId(int widgetId) {
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

        return mDatabase.query(
                QuoteDatabaseHelper.Tables.SETTINGS,  // The table to query
                projection,                               // The columns to return
                QuoteContract.SettingColumns.SETTING_WIDGET_ID + " = " + widgetId, // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
    }

    public Cursor getCursorModelsByWidgetId(int widgetId) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                QuoteContract.ModelColumns.MODEL_ID,
                QuoteContract.ModelColumns.MODEL_WIDGET_ID,
                QuoteContract.ModelColumns.MODEL_QUOTE_POSITION,
                QuoteContract.ModelColumns.MODEL_NAME,
                QuoteContract.ModelColumns.MODEL_RATE,
                QuoteContract.ModelColumns.MODEL_CHANGE,
                QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = QuoteContract.ModelColumns.MODEL_QUOTE_POSITION + " ASC";

        return mDatabase.query(
                QuoteDatabaseHelper.Tables.MODELS,  // The table to query
                projection,                               // The columns to return
                QuoteContract.ModelColumns.MODEL_WIDGET_ID + " = " + widgetId, // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
    }

    public Cursor getCursorAllSettings() {
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

        return mDatabase.query(
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
        int delCount = mDatabase.delete(QuoteDatabaseHelper.Tables.SETTINGS,
                QuoteContract.SettingColumns.SETTING_WIDGET_ID + " = " + widgetId, null);
        Log.i(LOG_TAG, "deleted rows count = " + delCount);
    }

    public void deleteSettingsById(String settingId) {
        int delCount = mDatabase.delete(QuoteDatabaseHelper.Tables.SETTINGS,
                QuoteContract.SettingColumns.SETTING_ID + " = '" + settingId + "'", null);
        Log.i(LOG_TAG, "deleted rows count = " + delCount);
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
        } while (cursor.moveToNext());

        cursor.close();

        return settings;
    }

    public List<Model> getModelsByWidgetId(int widgetId) {
        Cursor cursor = getCursorModelsByWidgetId(widgetId);
        if (0 >= cursor.getCount()) return new ArrayList();

        List list = new ArrayList(cursor.getCount());
        cursor.moveToFirst();
        do {
            Model model = transformCursorToModel(cursor);
            list.add(model);
        } while (cursor.moveToNext());

        cursor.close();

        return list;
    }

    private Setting transformCursorToSetting(Cursor cursor) {
        Setting setting = new Setting();
        setting.setId(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_ID)));
        setting.setWidgetId(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_WIDGET_ID)));
        setting.setQuotePosition(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_POSITION)));
        setting.setQuoteType(QuoteType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_TYPE))));
        setting.setQuoteSymbol(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL)));

        return setting;
    }

    private Model transformCursorToModel(Cursor cursor) {
        Model model = new Model();
        model.setName(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_NAME)));
        model.setRate(cursor.getDouble(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_RATE)));
        model.setChange(cursor.getDouble(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_CHANGE)));
        model.setPercentChange(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE)));

        return model;
    }

}
