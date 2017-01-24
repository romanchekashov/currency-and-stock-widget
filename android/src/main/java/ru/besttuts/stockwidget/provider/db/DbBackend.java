package ru.besttuts.stockwidget.provider.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.VisibleForTesting;

import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.QuoteDatabaseHelper;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 *         created on 1/24/2017.
 */

public class DbBackend implements DbContract {
    private static final String TAG = makeLogTag(DbBackend.class);

    private final QuoteDatabaseHelper mDbOpenHelper;

    DbBackend(Context context) {
        mDbOpenHelper = new QuoteDatabaseHelper(context);
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
}
