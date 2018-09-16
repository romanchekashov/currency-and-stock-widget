package ru.besttuts.stockwidget.util;

import android.content.Context;
import android.content.SharedPreferences;

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


}
