package ru.besttuts.stockwidget.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.ui.ConfigPreferenceFragment;

/**
 * Created by roman on 27.01.2015.
 */
public class Utils {

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
        String color = sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR, "#34495e");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
    }

}
