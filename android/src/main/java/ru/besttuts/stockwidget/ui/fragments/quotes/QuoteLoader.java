package ru.besttuts.stockwidget.ui.fragments.quotes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.provider.model.Quote;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class QuoteLoader extends AsyncTaskLoader<List<Quote>> {
    protected static String TAG = makeLogTag(QuoteLoader.class);

    private int mQuoteType;

    public QuoteLoader(@NonNull Context context, int quoteType) {
        super(context);
        mQuoteType = quoteType;
    }

    @Nullable
    @Override
    public List<Quote> loadInBackground() {
//        List<Quote> quotes = DbProvider.getInstance().getDatabaseAdapter()
//                .getQuotesByType(mQuoteType);

//        LOGD(TAG, String.format("loadInBackground: quoteType = %s, count = %d",
//                mQuoteType, quotes.size()));

        return new ArrayList<>();
    }
}
