package ru.besttuts.stockwidget.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import ru.besttuts.stockwidget.ui.fragments.ConfigPreferenceFragment;

/**
 * @author rchekashov
 * created on 10/7/2016.
 */
public class SharedPreferencesHelper {
    public static final String PREFS_NAME = "ru.besttuts.stockwidget.ui.EconomicWidget";

    public static SharedPreferences.Editor getWriter(Context context) {
        return context.getSharedPreferences(PREFS_NAME, 0).edit();
    }

    public static SharedPreferences getReader(Context context) {
        return context.getSharedPreferences(PREFS_NAME, 0);
    }

    public static void delete(String preferenceKey, Object value, Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(preferenceKey).apply();
    }

    public static void update(String preferenceKey, Object value, Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        if (value instanceof String) {
            prefs.putString(preferenceKey, (String) value);
        } else if (value instanceof Integer) {
            prefs.putInt(preferenceKey, (Integer) value);
        } else if (value instanceof Long) {
            prefs.putLong(preferenceKey, (Long) value);
        }
        prefs.apply();
    }

    public static Object get(String preferenceKey, Object defaultValue, Context context) {
        if (defaultValue == null) return null;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        if (defaultValue instanceof String) {
            return prefs.getString(preferenceKey, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return prefs.getInt(preferenceKey, (Integer) defaultValue);
        } else if (defaultValue instanceof Long) {
            return prefs.getLong(preferenceKey, (Long) defaultValue);
        }
        return new Object();
    }

    public static int getWidgetBgColor(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int seekBarValue = sharedPref.getInt(ConfigPreferenceFragment.KEY_PREF_BG_VISIBILITY,
                ConfigPreferenceFragment.KEY_PREF_BG_VISIBILITY_DEFAULT_VALUE);
        String visibility = Integer.toHexString(Math.round(seekBarValue * 2.55f));

        if (2 != visibility.length()) {
            visibility = "0" + visibility;
        }

        return Color.parseColor("#" + visibility + sharedPref.getString(
                ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE)
                .substring(1));
    }
}
