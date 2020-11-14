package ru.besttuts.stockwidget.ui.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import ru.besttuts.stockwidget.R;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 25.01.2015.
 */
public class DataCleanPreference extends DialogPreference {
    private static final String TAG = makeLogTag(DataCleanPreference.class);

    public DataCleanPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.data_clean_preference);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }
}
