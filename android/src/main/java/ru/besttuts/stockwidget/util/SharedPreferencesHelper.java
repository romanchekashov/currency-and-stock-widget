package ru.besttuts.stockwidget.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import ru.besttuts.stockwidget.sync.sparklab.dto.MobileQuoteFilter;
import ru.besttuts.stockwidget.ui.fragments.ConfigPreferenceFragment;

/**
 * @author rchekashov
 * created on 10/7/2016.
 */
public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "ru.besttuts.stockwidget.ui.EconomicWidget";

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

    public static MobileQuoteFilter getMobileQuoteFilter(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String currencies = sharedPref.getString("currencies", null);
        String stocks = sharedPref.getString("stocks", null);
        String futures = sharedPref.getString("futures", null);
        MobileQuoteFilter filter = null;
        if (currencies != null || stocks != null || futures != null) {
            filter = new MobileQuoteFilter();
            filter.setCurrencies(Utils.split(",", currencies));
            filter.setStocks(Utils.split(",", stocks));
            filter.setFutures(Utils.split(",", futures));
        }
        return filter;
    }

    public static void saveMobileQuoteFilter(Context context, MobileQuoteFilter filter) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefs = sharedPref.edit();
        prefs.putString("currencies", Utils.join(",", filter.getCurrencies()));
        prefs.putString("stocks", Utils.join(",", filter.getStocks()));
        prefs.putString("futures", Utils.join(",", filter.getFutures()));
        prefs.apply();
    }
}
