package ru.besttuts.stockwidget.ui.fragments;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.ui.EconomicWidget;
import ru.besttuts.stockwidget.util.NotificationManager;
import ru.besttuts.stockwidget.util.SharedPreferencesHelper;

/**
 * Created by roman on 22.01.2015.
 */
public class ConfigPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = makeLogTag(ConfigPreferenceFragment.class);

    public static final String KEY_PREF_UPDATE_VIA = "pref_listUpdateVia";
    public static final String KEY_PREF_UPDATE_VIA_DEFAULT_VALUE_WI_FI = "wi-fi";

    public static final int KEY_PREF_UPDATE_INTERVAL_MIN_VALUE = 900000;
    public static final String KEY_PREF_UPDATE_INTERVAL = "pref_listUpdateInterval";
    public static final String KEY_PREF_UPDATE_INTERVAL_DEFAULT_VALUE = "1800000";

    public static final String KEY_PREF_BG_COLOR = "pref_listBgColor";
    public static final String KEY_PREF_BG_COLOR_DEFAULT_VALUE = "#34495e";

    public static final String KEY_PREF_BG_VISIBILITY = "pref_listBgVisibility";
    public static final String KEY_PREF_BG_VISIBILITY_DEFAULT_VALUE = "C0";

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.preference_config);

        ListPreference listPreference = (ListPreference) findPreference(KEY_PREF_UPDATE_INTERVAL);
        if (listPreference.getEntry() == null) {
            listPreference.setValue("" + KEY_PREF_UPDATE_INTERVAL_MIN_VALUE);
            listPreference.setSummary(listPreference.getEntry());
            SharedPreferencesHelper.update(KEY_PREF_UPDATE_INTERVAL, "" + KEY_PREF_UPDATE_INTERVAL_MIN_VALUE, getActivity());
            EconomicWidget.setAlarm(getActivity());
        }
        listPreference.setSummary(listPreference.getEntry());

        listPreference = (ListPreference) findPreference(KEY_PREF_BG_COLOR);
        listPreference.setSummary(listPreference.getEntry());

        listPreference = (ListPreference) findPreference(KEY_PREF_UPDATE_VIA);
        listPreference.setSummary(listPreference.getEntry());

        LOGD(TAG, "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_BG_COLOR)) {
            ListPreference listPreference = (ListPreference) findPreference(key);
            // Set Ccy to be the user-description for the selected value
            listPreference.setSummary(listPreference.getEntry());

            NotificationManager.notifyColorChangedListeners();

            return;
        }

        if (key.equals(KEY_PREF_UPDATE_INTERVAL)) {
            ListPreference listPreference = (ListPreference) findPreference(key);
            // Set Ccy to be the user-description for the selected value
            listPreference.setSummary(listPreference.getEntry());

            EconomicWidget.setAlarm(getActivity());

            return;
        }

        if (key.equals(KEY_PREF_UPDATE_VIA)) {
            ListPreference listPreference = (ListPreference) findPreference(key);
            // Set Ccy to be the user-description for the selected value
            listPreference.setSummary(listPreference.getEntry());

            return;
        }
    }

    public static long getUpdateInterval(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        long interval = Integer.parseInt(sharedPreferences.getString(
                ConfigPreferenceFragment.KEY_PREF_UPDATE_INTERVAL,
                ConfigPreferenceFragment.KEY_PREF_UPDATE_INTERVAL_DEFAULT_VALUE));

        if (interval > 0 && interval < KEY_PREF_UPDATE_INTERVAL_MIN_VALUE) {
            interval = KEY_PREF_UPDATE_INTERVAL_MIN_VALUE;
        }

        return interval;
    }
}
