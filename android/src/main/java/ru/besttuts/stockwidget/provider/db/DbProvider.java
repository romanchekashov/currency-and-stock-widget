package ru.besttuts.stockwidget.provider.db;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

import java.util.List;

import ru.besttuts.stockwidget.provider.AppDatabase;
import ru.besttuts.stockwidget.provider.dao.ModelDao;
import ru.besttuts.stockwidget.provider.dao.QuoteDao;
import ru.besttuts.stockwidget.provider.dao.QuoteProviderDao;
import ru.besttuts.stockwidget.provider.model.Quote;
import ru.besttuts.stockwidget.sync.sparklab.dto.MobileQuoteShort;

/**
 * @author rchekashov
 * created on 1/24/2017.
 */

public class DbProvider extends DbProviderAbs {

    public interface ResultCallback<T> {
        void onFinished(T result);
    }

    private DbProvider(Context context) {
        super(context);
    }

    @VisibleForTesting
    DbProvider(AppDatabase dbBackend,
               DbBackendAdapterImpl dbBackendAdapter,
               CustomExecutor executor) {
        super(dbBackend, dbBackendAdapter, executor);
    }

    public static void init(Context context) {
        if (null == sDbProviderInstance) {
            sDbProviderInstance = new DbProvider(context);
        }
    }

    public static DbProvider getInstance() {
        if (null == sDbProviderInstance) {
            throw new RuntimeException("before use call DbProvider.init(context) from Application.onCreate()");
        }
        return sDbProviderInstance;
    }

    public static AppDatabase getDatabase() {
        return getInstance().database;
    }

    public static ModelDao modelDao() {
        return getDatabase().modelDao();
    }

    public static QuoteDao quoteDao() {
        return getDatabase().quoteDao();
    }

    public static QuoteProviderDao quoteProviderDao() {
        return getDatabase().quoteProviderDao();
    }

    public boolean saveQuotes(List<MobileQuoteShort> quoteShorts) {
        Quote[] quotes = new Quote[quoteShorts.size()];
        int i = 0;
        for (MobileQuoteShort q : quoteShorts) {
            Quote quote = new Quote();
            quote.setQuoteSymbol(q.getS());
            quote.setQuoteName(q.getN());
            quote.setQuoteType(String.valueOf(q.getQt()));
            quotes[i++] = quote;
        }
        quoteDao().insertAll(quotes);
        return true;
    }
}
