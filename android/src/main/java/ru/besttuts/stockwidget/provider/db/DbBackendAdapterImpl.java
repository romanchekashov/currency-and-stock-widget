package ru.besttuts.stockwidget.provider.db;

import android.content.Context;

import java.util.List;

import ru.besttuts.stockwidget.provider.AppDatabase;
import ru.besttuts.stockwidget.provider.dao.SettingDao;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Quote;
import ru.besttuts.stockwidget.provider.model.QuoteProvider;
import ru.besttuts.stockwidget.provider.model.Setting;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 * created on 1/24/2017.
 */

class DbBackendAdapterImpl implements DbBackendAdapter {
    private static final String TAG = makeLogTag(DbBackendAdapterImpl.class);

    private AppDatabase database;

    public DbBackendAdapterImpl(Context context) {
        database = AppDatabase.getInstance(context);
    }

    public DbBackendAdapterImpl(AppDatabase database) {
        this.database = database;
    }

    @Override
    public void deleteAll() {
        SettingDao settingDao = database.settingDao();
        List<Setting> settings = settingDao.getAll();
        settingDao.deleteAll(settings.toArray(new Setting[settings.size()]));

        database.modelDao().deleteAll();
    }

    @Override
    public List<Setting> getAllSettings() {
        return database.settingDao().getAll();
    }

    @Override
    public void deleteSettingsByWidgetId(int widgetId) {
        List<Setting> settings = database.settingDao().allByWidgetId(widgetId);
        database.settingDao().deleteAll(settings.toArray(new Setting[settings.size()]));
    }

    @Override
    public void deleteSettingsByIdAndUpdatePositions(String settingId, int position) {
        //TODO impl
    }

    @Override
    public Model getModelById(String modelId) {
        return database.modelDao().byId(modelId);
    }

    @Override
    public void deleteQuotesByIds(String[] symbols) {
        List<Quote> quotes = database.quoteDao().getAllBySymbols(symbols);
        database.quoteDao().deleteAll(quotes.toArray(new Quote[quotes.size()]));
    }

    @Override
    public List<QuoteProvider> getQuoteProviders() {
        return database.quoteProviderDao().getAll();
    }

    @Override
    public List<Setting> getSettingsByWidgetId(int widgetId) {
        return database.settingDao().allByWidgetId(widgetId);
    }
}
