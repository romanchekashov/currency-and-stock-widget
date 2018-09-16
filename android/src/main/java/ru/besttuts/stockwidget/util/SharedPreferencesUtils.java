package ru.besttuts.stockwidget.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.besttuts.stockwidget.R;

import static ru.besttuts.stockwidget.Config.PREFS_NAME;
import static ru.besttuts.stockwidget.Config.PREF_PREFIX_KEY;

public class SharedPreferencesUtils {

    public static class Density {
        private static final String KEY = PREF_PREFIX_KEY + "_density";
        private static float density = -1;

        public static void save(Context context, float density) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
            prefs.putFloat(KEY, density);
            prefs.commit();
        }

        public static float load(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            if (density < 0) {
                density = prefs.getFloat(KEY, 1);
            }
            return density;
        }
    }

    public static class Rows {
        private static final String KEY = PREF_PREFIX_KEY + "_rows";

        public static void save(Context context, int rows) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
            prefs.putInt(KEY, rows);
            prefs.commit();
        }

        public static int load(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            return prefs.getInt(KEY, 1);
        }
    }

    public static class WidgetLayout {
        private static final String KEY_TMPL = PREF_PREFIX_KEY + "%d_widget_layout";

        public static void save(Context context, int appWidgetId, int layout) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
            prefs.putInt(String.format(KEY_TMPL, appWidgetId), layout);
            prefs.commit();
        }

        public static int load(Context context, int appWidgetId) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            return prefs.getInt(String.format(KEY_TMPL, appWidgetId), R.layout.economic_widget);
        }

        public static void delete(Context context, int appWidgetId) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
            prefs.remove(String.format(KEY_TMPL, appWidgetId));
            prefs.commit();
        }
    }

    public static class WidgetLayoutGridItem {
        private static final String KEY_TMPL = PREF_PREFIX_KEY + "%d_widget_layout_grid_item";

        public static void save(Context context, int appWidgetId, int layoutGridItem) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
            prefs.putInt(String.format(KEY_TMPL, appWidgetId), layoutGridItem);
            prefs.commit();
        }

        public static int load(Context context, int appWidgetId) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            return prefs.getInt(String.format(KEY_TMPL, appWidgetId), R.layout.economic_widget_item);
        }

        public static void delete(Context context, int appWidgetId) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
            prefs.remove(String.format(KEY_TMPL, appWidgetId));
            prefs.commit();
        }
    }

    public static class LastUpdateTime {
        private static final String KEY_TMPL = PREF_PREFIX_KEY + "%d_lastupdatetime";

        public static void save(Context context, int appWidgetId, String text) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
            prefs.putString(String.format(KEY_TMPL, appWidgetId), text);
            prefs.commit();
        }

        public static String load(Context context, int appWidgetId) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            String titleValue = prefs.getString(String.format(KEY_TMPL, appWidgetId), null);
            if (titleValue != null) {
                return titleValue;
            } else {
                return new SimpleDateFormat().format(Calendar.getInstance().getTime());
            }
        }

        public static void delete(Context context, int appWidgetId) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
            prefs.remove(String.format(KEY_TMPL, appWidgetId));
            prefs.commit();
        }
    }

}
