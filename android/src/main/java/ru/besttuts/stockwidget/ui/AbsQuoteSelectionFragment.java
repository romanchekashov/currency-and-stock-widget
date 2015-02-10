package ru.besttuts.stockwidget.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 10.02.2015.
 */
public abstract class AbsQuoteSelectionFragment extends Fragment implements IQuoteTypeFragment {

    protected static String TAG = makeLogTag(AbsQuoteSelectionFragment.class);

    private static final String ARG_SYMBOLS = "symbols";
    private static final String ARG_QUOTE_TYPE = "quoteType";

    protected QuoteType mQuoteType;
    protected Set<String> mSymbols;

    // подключение к БД (DAO)
    protected QuoteDataSource mDataSource;

    // цвет для выделенного элемента списка
    private int mColor;

    public AbsQuoteSelectionFragment() {}

    @Override
    public String[] getSelectedSymbols() {
        if (null == mSymbols) return new String[0];

        String[] symbols = new String[mSymbols.size()];
        return mSymbols.toArray(symbols);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSymbols = new HashSet<>();
        if (getArguments() != null) {
            mQuoteType = QuoteType.valueOf(getArguments().getString(ARG_QUOTE_TYPE));
        }
        if (null != savedInstanceState) {
            mSymbols.addAll(savedInstanceState.getStringArrayList(ARG_SYMBOLS));
        }
        mDataSource = new QuoteDataSource(getActivity());
        mDataSource.open();

        LOGD(TAG, "onCreate: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> list = new ArrayList<String>(mSymbols.size());
        list.addAll(mSymbols);
        outState.putStringArrayList(ARG_SYMBOLS, list);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mDataSource) mDataSource.close();
        LOGD(TAG, "onDestroy: ");
    }

    private int getColor() {
        if (0 == mColor) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String bgColor = "#" + ConfigPreferenceFragment.KEY_PREF_BG_VISIBILITY_DEFAULT_VALUE +
                    sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                            ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE).substring(1);
            mColor = Color.parseColor(bgColor);
        }

        return mColor;
    }

    protected void setSelectedBgView(View view) {
        view.setBackgroundColor(getColor());
    }

    protected class MySimpleCursorAdapter extends SimpleCursorAdapter {
        MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            String symbol = String.valueOf(((TextView) view.findViewById(android.R.id.text2)).getText());
            if (mSymbols.contains(symbol)) {
                setSelectedBgView(view);
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }

            ((TextView) view.findViewById(android.R.id.text1)).setText(
                    Utils.getModelNameFromResourcesBySymbol(getActivity(), symbol));

            return view;
        }
    }

    protected static class MyCursorLoader extends CursorLoader {

        QuoteDataSource mDataSource;
        private QuoteType mQuoteType;

        public MyCursorLoader(Context context, QuoteDataSource dataSource,
                              QuoteType quoteType) {
            super(context);
            mDataSource = dataSource;
            mQuoteType = quoteType;
        }

        @Override
        public Cursor loadInBackground() {
            QuoteDataSource dataSource = new QuoteDataSource(getContext());
            dataSource.open();
            Cursor cursor = null;
            try {
                cursor = mDataSource.getQuoteCursor(mQuoteType);

                LOGD(TAG, String.format("loadInBackground: quoteType = %s, count = %d",
                        mQuoteType, cursor.getCount()));
            } finally {
                dataSource.close();
            }

            return cursor;
        }

    }

}
