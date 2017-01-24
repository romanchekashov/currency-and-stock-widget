package ru.besttuts.stockwidget.provider.db;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.model.Model;

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
}
