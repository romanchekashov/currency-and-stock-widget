package ru.besttuts.stockwidget.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.util.NotificationManager;

/**
 * Created by roman on 22.01.2015.
 */
public class ConfigPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_BG_COLOR = "pref_listBgColor";

    public static final String KEY_PREF_BG_VISIBILITY = "pref_listBgVisibility";

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.preference_config);
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
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, "#34495e"));

            NotificationManager.notifyColorChangedListeners();
        }
    }
}
