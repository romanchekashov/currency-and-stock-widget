package ru.besttuts.stockwidget.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.ui.preferences.SliderPreference;
import ru.besttuts.stockwidget.util.NotificationManager;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 22.01.2015.
 */
public class ConfigPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = makeLogTag(ConfigPreferenceFragment.class);

    public static final String KEY_PREF_UPDATE_VIA = "pref_listUpdateVia";
    public static final String KEY_PREF_UPDATE_VIA_DEFAULT_VALUE_WI_FI = "wi-fi";

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
}
