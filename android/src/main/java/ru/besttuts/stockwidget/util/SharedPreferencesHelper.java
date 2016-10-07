package ru.besttuts.stockwidget.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author rchekashov
 *         created on 10/7/2016.
 */

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "ru.besttuts.stockwidget.ui.EconomicWidget";

    public static void delete(String preferenceKey, Object value, Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(preferenceKey).apply();
    }

    public static void update(String preferenceKey, Object value, Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        if(value instanceof String){
            prefs.putString(preferenceKey, (String) value);
        } else if(value instanceof Integer){
            prefs.putInt(preferenceKey, (Integer) value);
        } else if(value instanceof Long){
            prefs.putLong(preferenceKey, (Long) value);
        }
        prefs.apply();
    }

    public static Object get(String preferenceKey, Object defaultValue, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        if(defaultValue instanceof String){
            return prefs.getString(preferenceKey, (String) defaultValue);
        } else if(defaultValue instanceof Integer){
            return prefs.getInt(preferenceKey, (Integer) defaultValue);
        } else if(defaultValue instanceof Long){
            return prefs.getLong(preferenceKey, (Long) defaultValue);
        }
        return new Object();
    }
}
