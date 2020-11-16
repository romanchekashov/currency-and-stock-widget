package ru.besttuts.stockwidget.provider.db;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import ru.besttuts.stockwidget.provider.AppDatabase;
import ru.besttuts.stockwidget.provider.dao.ModelDao;
import ru.besttuts.stockwidget.provider.dao.QuoteDao;
import ru.besttuts.stockwidget.provider.dao.QuoteProviderDao;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Quote;
import ru.besttuts.stockwidget.sync.sparklab.dto.MobileQuoteShort;
import ru.besttuts.stockwidget.util.CustomConverter;

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
            quotes[i++] = CustomConverter.toQuote(q);
        }
        quoteDao().insertAll(quotes);
        return true;
    }

    private List<Quote> quotesWaitingToBeSaved = new ArrayList<>();

    public void addTempQuotes(Quote quote) {
        quotesWaitingToBeSaved.add(quote);
    }

    public void removeTempQuotes(Quote quote) {
        quotesWaitingToBeSaved.remove(quote);
    }

    public Completable saveTempQuotes(int widgetId) {
        return Completable.create(emitter -> {
            List<Model> models = modelDao().allByWidgetId(widgetId);
            int maxSort = models.isEmpty() ? 0 : models.get(models.size() - 1).getSort();

            Model[] inserting = new Model[quotesWaitingToBeSaved.size()];
            int insertingIdx = 0;
            for (Quote q : quotesWaitingToBeSaved) {
                Model m = CustomConverter.toModel(q);
                m.setWidgetId(widgetId);
                m.setSort(++maxSort);
                inserting[insertingIdx++] = m;
            }

            modelDao().insertAll(inserting);

            emitter.onComplete();
        });
    }

    public List<Model> updateModels(int widgetId, List<MobileQuoteShort> quotes) {
        List<Model> models = modelDao().allByWidgetId(widgetId);
        Map<Integer, Model> ids = new HashMap<>(models.size());
        for (Model model : models) ids.put(model.getId(), model);

        Model[] updating = new Model[ids.size()];
        int updatingIdx = 0;
        for (MobileQuoteShort q : quotes) {
            if (ids.containsKey(q.getI())) {
                Model m = CustomConverter.toModel(q);
                m.setWidgetId(widgetId);
                m.setSort(ids.get(q.getI()).getSort());
                updating[updatingIdx++] = m;
            }
        }
        modelDao().updateAll(updating);

        return modelDao().allByWidgetId(widgetId);
    }
}
