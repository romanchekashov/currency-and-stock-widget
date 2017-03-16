package ru.besttuts.stockwidget.provider.db;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.model.SettingModel;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 *         created on 1/24/2017.
 */

public class DbBackendAdapter implements DbContract {
    private static final String TAG = makeLogTag(DbBackendAdapter.class);

    private final DbBackend mDbBackend;

    DbBackendAdapter(Context context) {
        mDbBackend = new DbBackend(context);
    }

    DbBackendAdapter(DbBackend dbBackend) {
        mDbBackend = dbBackend;
    }

    List<Setting> getAllSettings(){
        Cursor cursor = mDbBackend.getAllSettings();
        if (0 >= cursor.getCount()) return new ArrayList<>();

        List<Setting> list = new ArrayList<>(cursor.getCount());
        cursor.moveToFirst();
        do {
            list.add(Setting.map(cursor));
        } while (cursor.moveToNext());

        return list;
    }

    void addSettingsRec(int mAppWidgetId, int widgetItemPosition,
                        int type, String[] symbols){
        for (int i = 0; i < symbols.length; i++) {
            String symbol = symbols[i];
            int position = widgetItemPosition + i;
            String id = mAppWidgetId + "_" + position;

            Setting setting = new Setting();
            setting.setId(id);
            setting.setWidgetId(mAppWidgetId);
            setting.setQuotePosition(position);
            setting.setQuoteType(type);
            setting.setQuoteSymbol(symbol);

            mDbBackend.persist(setting);
        }
    }

    Model getModelById(String modelId) {
        Cursor cursor = mDbBackend.getModelById(modelId);
        if (0 >= cursor.getCount()) return null;

        Model model;

        cursor.moveToFirst();
        do {
            model = Model.map(cursor);
        } while (cursor.moveToNext());

        return model;
    }

    public List<Model> getModelsByWidgetId(int widgetId) {
        Cursor cursor = mDbBackend.getCursorModelsByWidgetId(widgetId);
        if (0 >= cursor.getCount()) return new ArrayList<Model>();

        List<Model> list = new ArrayList<Model>(cursor.getCount());
        cursor.moveToFirst();
        do {
            list.add(Model.map(cursor));
        } while (cursor.moveToNext());

        return list;
    }

    List<Setting> getSettingsByWidgetId(int widgetId) {
        Cursor cursor = mDbBackend.getCursorSettingsByWidgetId(widgetId);
        if (0 >= cursor.getCount()) return new ArrayList<>();

        List<Setting> list = new ArrayList<>(cursor.getCount());
        cursor.moveToFirst();
        do {
            list.add(Setting.map(cursor));
        } while (cursor.moveToNext());

        return list;
    }

    List<SettingModel> getSettingsWithModelByWidgetId(int widgetId){
        final Cursor cursor = mDbBackend.getCursorSettingsWithModelByWidgetId(widgetId);
        if (0 >= cursor.getCount()) return new ArrayList<>();

        List<SettingModel> list = new ArrayList<>(cursor.getCount());
        cursor.moveToFirst();
        do {
            list.add(SettingModel.map(cursor));
        } while (cursor.moveToNext());

        return list;
    }
}
