package ru.besttuts.stockwidget.ui.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.QuoteDataSource;

/**
 * Created by roman on 25.01.2015.
 */
public class DataCleanPreference extends DialogPreference {

    public DataCleanPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.data_clean_preference);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            QuoteDataSource dataSource = new QuoteDataSource(getContext());
            dataSource.open();
            dataSource.deleteAll();
            dataSource.close();
        }
    }
}
