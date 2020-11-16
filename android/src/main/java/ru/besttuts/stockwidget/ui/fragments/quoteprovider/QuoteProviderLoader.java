package ru.besttuts.stockwidget.ui.fragments.quoteprovider;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.provider.model.QuoteProvider;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class QuoteProviderLoader extends AsyncTaskLoader<List<QuoteProvider>> {
    protected static String TAG = makeLogTag(QuoteProviderLoader.class);

    public QuoteProviderLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public List<QuoteProvider> loadInBackground() {
        return DbProvider.quoteProviderDao().getAll();
    }
}
