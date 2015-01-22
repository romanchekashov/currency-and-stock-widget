package ru.besttuts.stockwidget.ui;

import android.os.Bundle;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import ru.besttuts.stockwidget.R;

/**
 * Created by roman on 22.01.2015.
 */
public class ConfigPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.preference_config);
    }
}
