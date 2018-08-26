package ru.besttuts.stockwidget.ui.fragments.quotes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

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
        List<Quote> quotes = DbProvider.getInstance().getDatabaseAdapter()
                .getQuotesByType(mQuoteType);

        LOGD(TAG, String.format("loadInBackground: quoteType = %s, count = %d",
                mQuoteType, quotes.size()));

        return quotes;
    }
}
