package ru.besttuts.stockwidget.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.ui.ConfigPreferenceFragment;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 27.01.2015.
 */
public class Utils {

    private static final String TAG = makeLogTag(Utils.class);

    public static String getModelNameFromResourcesBySymbol(Context context, String symbol) {
        return getModelNameFromResourcesBySymbol(context, QuoteType.GOODS, symbol);
    }

    public static String getModelNameFromResourcesBySymbol(Context context, QuoteType type, String symbol) {
        if (null == context || null == type || null == symbol || symbol.isEmpty()) return "-";

        if (QuoteType.CURRENCY_EXCHANGE.equals(type)) {
            return symbol.substring(0, 3) + "/" + symbol.substring(3);
        }

        String field = symbol.toLowerCase().replace(".", "_");
        try {
            return context.getString(R.string.class.getDeclaredField(field).getInt(null));
        } catch (IllegalAccessException e) {
            LOGE(TAG, e.getMessage());
        } catch (NoSuchFieldException e) {
            LOGE(TAG, e.getMessage());
        }

        return symbol;
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String style = sharedPref.getString("theme", "light");
        if ("light".equals(style)) {
            activity.setTheme(R.style.AppTheme_Light);
        } else {
            activity.setTheme(R.style.AppTheme_Dark);
        }
    }

    public static void onActivityCreateSetActionBarColor(ActionBar actionBar) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(actionBar.getThemedContext());
        String color = sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
    }

}
