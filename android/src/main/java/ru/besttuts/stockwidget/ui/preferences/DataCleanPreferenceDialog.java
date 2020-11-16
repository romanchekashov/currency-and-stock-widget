package ru.besttuts.stockwidget.ui.preferences;

import android.os.Bundle;

import androidx.preference.PreferenceDialogFragmentCompat;

import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.ui.fragments.tracking.TrackingQuotesFragment;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 25.01.2015.
 */
public class DataCleanPreferenceDialog extends PreferenceDialogFragmentCompat {
    private static final String TAG = makeLogTag(DataCleanPreferenceDialog.class);

    public static DataCleanPreferenceDialog newInstance(String key) {
        DataCleanPreferenceDialog fragment = new DataCleanPreferenceDialog();
        Bundle args = new Bundle();
        args.putString(PreferenceDialogFragmentCompat.ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            DbProvider.modelDao().deleteAll();
            TrackingQuotesFragment.mWidgetItemsNumber = 0;
            LOGD(TAG, "Data cleaned!");
        }
    }

//    @Override
//    protected View onCreateDialogView() {
//        View view = super.onCreateDialogView();
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            ((TextView) view.findViewById(R.id.pref_dataclean_textView)).setTextColor(Color.WHITE);
//        }
//        return view;
//    }

}
