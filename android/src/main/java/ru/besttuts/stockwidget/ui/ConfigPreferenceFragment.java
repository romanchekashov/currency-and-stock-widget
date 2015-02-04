package ru.besttuts.stockwidget.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.util.NotificationManager;

/**
 * Created by roman on 22.01.2015.
 */
public class ConfigPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_UPDATE_VIA = "pref_listUpdateVia";
    public static final String KEY_PREF_UPDATE_VIA_DEFAULT_VALUE_WI_FI = "wi-fi";

    public static final String KEY_PREF_UPDATE_INTERVAL = "pref_listUpdateInterval";
    public static final String KEY_PREF_UPDATE_INTERVAL_DEFAULT_VALUE = "1800000";

    public static final String KEY_PREF_BG_COLOR = "pref_listBgColor";
    public static final String KEY_PREF_BG_COLOR_DEFAULT_VALUE = "#3498db";

    public static final String KEY_PREF_BG_VISIBILITY = "pref_listBgVisibility";
    public static final String KEY_PREF_BG_VISIBILITY_DEFAULT_VALUE = "80";

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.preference_config);

        ListPreference listPreference = (ListPreference) findPreference(KEY_PREF_UPDATE_INTERVAL);
        listPreference.setSummary(listPreference.getEntry());

        listPreference = (ListPreference) findPreference(KEY_PREF_BG_COLOR);
        listPreference.setSummary(listPreference.getEntry());

        listPreference = (ListPreference) findPreference(KEY_PREF_BG_VISIBILITY);
        listPreference.setSummary(listPreference.getEntry());

        listPreference = (ListPreference) findPreference(KEY_PREF_UPDATE_VIA);
        listPreference.setSummary(listPreference.getEntry());
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
            // Set summary to be the user-description for the selected value
            listPreference.setSummary(listPreference.getEntry());

            NotificationManager.notifyColorChangedListeners();

            return;
        }

        if (key.equals(KEY_PREF_BG_VISIBILITY)) {
            ListPreference listPreference = (ListPreference) findPreference(key);
            // Set summary to be the user-description for the selected value
            listPreference.setSummary(listPreference.getEntry());

            return;
        }

        if (key.equals(KEY_PREF_UPDATE_INTERVAL)) {
            ListPreference listPreference = (ListPreference) findPreference(key);
            // Set summary to be the user-description for the selected value
            listPreference.setSummary(listPreference.getEntry());

            EconomicWidget.setAlarm(getActivity());

            return;
        }

        if (key.equals(KEY_PREF_UPDATE_VIA)) {
            ListPreference listPreference = (ListPreference) findPreference(key);
            // Set summary to be the user-description for the selected value
            listPreference.setSummary(listPreference.getEntry());

            return;
        }
    }
}
