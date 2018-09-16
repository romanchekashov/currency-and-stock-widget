package ru.besttuts.stockwidget.ui.fragments.quoteprovider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.provider.model.QuoteProvider;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class QuoteProviderLoader extends AsyncTaskLoader<List<QuoteProvider>> {
    protected static String TAG = makeLogTag(QuoteProviderLoader.class);

    private int mQuoteType;

    public QuoteProviderLoader(@NonNull Context context, int quoteType) {
        super(context);
        mQuoteType = quoteType;
    }

    @Nullable
    @Override
    public List<QuoteProvider> loadInBackground() {
        List<QuoteProvider> providers = DbProvider.getInstance().getDatabaseAdapter()
                .getQuoteProviders();
        return providers;
    }
}
