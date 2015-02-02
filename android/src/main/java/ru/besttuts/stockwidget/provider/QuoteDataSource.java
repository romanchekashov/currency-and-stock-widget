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
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
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

        LOGD(TAG, "insertWithOnConflict rows count = " + count);
    }

    public void addModelRec(QuoteType type, String symbol) {

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(QuoteContract.ModelColumns.MODEL_ID, symbol);
        values.put(QuoteContract.ModelColumns.MODEL_NAME,
                Utils.getModelNameFromResourcesBySymbol(context, type, symbol));
        values.put(QuoteContract.ModelColumns.MODEL_RATE, 0);
        values.put(QuoteContract.ModelColumns.MODEL_CHANGE, 0);
        values.put(QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE, "");

        // Which row to update, based on the ID
        String selection = QuoteContract.ModelColumns.MODEL_ID + " LIKE ?";
        String[] selectionArgs = { symbol };

        long count = mDatabase.insertWithOnConflict(
                QuoteDatabaseHelper.Tables.MODELS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);

        LOGD(TAG, "insertWithOnConflict rows count = " + count);
    }

    public void addModelRec(Model model) {

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(QuoteContract.ModelColumns.MODEL_ID, model.getId());
        values.put(QuoteContract.ModelColumns.MODEL_NAME, model.getName());
        values.put(QuoteContract.ModelColumns.MODEL_RATE, model.getRate());
        values.put(QuoteContract.ModelColumns.MODEL_CHANGE, model.getChange());
        values.put(QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE, model.getPercentChange());

        // Which row to update, based on the ID
        String selection = QuoteContract.ModelColumns.MODEL_ID + " LIKE ?";
        String[] selectionArgs = { model.getId() };

        long count = mDatabase.insertWithOnConflict(
                QuoteDatabaseHelper.Tables.MODELS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);

        LOGD(TAG, "insertWithOnConflict rows count = " + count);
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

    /**
     * Получаем Cursor настроек вместе с данными котировки, если они есть
     * Пример использования INNER JOIN
     *
     * @param widgetId идентификатор виджета
     * @return Cursor
     */
    public Cursor getCursorSettingsWithModelByWidgetId(int widgetId) {

        String sqlQuery = "select * "
                + "from "+QuoteDatabaseHelper.Tables.SETTINGS+" as s "
                + "left join "+QuoteDatabaseHelper.Tables.MODELS+" as m "
                + "on s.setting_quote_symbol = m.model_id "
                + "where s.setting_widget_id = ? order by "
                + QuoteContract.SettingColumns.SETTING_QUOTE_POSITION + " asc";

        return mDatabase.rawQuery(sqlQuery, new String[] { String.valueOf(widgetId) });

    }

    public Cursor getCursorModelsByWidgetId(int widgetId) {

        String sqlQuery = "select m._id, m.model_id, m.model_name, "
                + "m.model_rate, m.model_change, m.model_percent_change "
                + "from "+QuoteDatabaseHelper.Tables.SETTINGS+" as s "
                + "inner join "+QuoteDatabaseHelper.Tables.MODELS+" as m "
                + "on s.setting_quote_symbol = m.model_id "
                + "where s.setting_widget_id = ? order by "
                + QuoteContract.SettingColumns.SETTING_QUOTE_POSITION + " asc";

        return mDatabase.rawQuery(sqlQuery, new String[] { String.valueOf(widgetId) });

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

        LOGD(TAG, "deleteSettingsByWidgetId: deleted rows count = " + delCount);

        // Если все записи Setting удалены, то удаляем все записи Model
        if (0 == mDatabase.rawQuery("select _id from "+ QuoteDatabaseHelper.Tables.SETTINGS, null).getCount()) {
            mDatabase.delete(QuoteDatabaseHelper.Tables.MODELS, null, null);
        }
    }

    public void deleteSettingsById(String settingId) {
        int delCount = mDatabase.delete(QuoteDatabaseHelper.Tables.SETTINGS,
                QuoteContract.SettingColumns.SETTING_ID + " = '" + settingId + "'", null);
        LOGD(TAG, "deleteSettingsById: deleted rows count = " + delCount);
    }

    public void deleteSettingsByIdAndUpdatePositions(String settingId, int position) {
        LOGD(TAG, String.format("deleteSettingsById: settingId = %s, position = %d",
                settingId, position));

        deleteSettingsById(settingId);

        Cursor cursor = mDatabase.rawQuery("select * from " + QuoteDatabaseHelper.Tables.SETTINGS
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
            mDatabase.update(QuoteDatabaseHelper.Tables.SETTINGS, args,
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
        mDatabase.delete(QuoteDatabaseHelper.Tables.SETTINGS, null, null);
        mDatabase.delete(QuoteDatabaseHelper.Tables.MODELS, null, null);
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

    public static Setting transformCursorToSetting(Cursor cursor) {
        Setting setting = new Setting();
        setting.setId(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_ID)));
        setting.setWidgetId(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_WIDGET_ID)));
        setting.setQuotePosition(cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_POSITION)));
        setting.setQuoteType(QuoteType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_TYPE))));
        setting.setQuoteSymbol(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL)));

        return setting;
    }

    public static Model transformCursorToModel(Cursor cursor) {
        Model model = new Model();
        model.setName(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_NAME)));
        model.setRate(cursor.getDouble(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_RATE)));
        model.setChange(cursor.getDouble(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_CHANGE)));
        model.setPercentChange(cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE)));

        return model;
    }

}
