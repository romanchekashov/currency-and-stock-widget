package ru.besttuts.stockwidget.ui.preferences;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxrelay.PublishRelay;

import ru.besttuts.stockwidget.Events;
import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.QuoteDataSource;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
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

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            QuoteDataSource dataSource = new QuoteDataSource(getContext());
            dataSource.deleteAll();
            dataSource.close();

            LOGD(TAG, "onDialogClosed");
            PublishRelay<Integer> relay = PublishRelay.create();
            relay.call(Events.ALL_DB_DATA_DELETED);
        }
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ((TextView) view.findViewById(R.id.pref_dataclean_textView)).setTextColor(Color.WHITE);
        }
        return view;
    }

}
