package ru.besttuts.stockwidget.provider.db.impl;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.besttuts.stockwidget.provider.AppDatabase;
import ru.besttuts.stockwidget.provider.dao.ModelDao;
import ru.besttuts.stockwidget.provider.dao.SettingDao;
import ru.besttuts.stockwidget.provider.db.DbBackendAdapter;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Quote;
import ru.besttuts.stockwidget.provider.model.QuoteProvider;
import ru.besttuts.stockwidget.provider.model.Setting;
import ru.besttuts.stockwidget.provider.model.wrap.ModelSetting;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 *         created on 1/24/2017.
 */

public class DbBackendAdapterImpl implements DbBackendAdapter {
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

        ModelDao modelDao = database.modelDao();
        List<Model> models = modelDao.getAll();
        modelDao.deleteAll(models.toArray(new Model[models.size()]));
    }

    @Override
    public List<Setting> getAllSettings() {
        return database.settingDao().getAll();
    }

    @Override
    public List<Setting> getSettingsWithoutModelByWidgetId(int widgetId) {
        List<Setting> settings = database.settingDao().allByWidgetId(widgetId);
        List<Model> models = getModelsByWidgetId(widgetId);
        Set<String> symbols = new HashSet<>();
        for (Model model: models) symbols.add(model.getId());

        List<Setting> settingsWithoutModel = new ArrayList<>();
        for (Setting setting: settings) {
            if (!symbols.contains(setting.getQuoteSymbol())) {
                settingsWithoutModel.add(setting);
            }
        }

        return settingsWithoutModel;
    }

    @Override
    public void addSettingsRec(int mAppWidgetId, int widgetItemPosition,
                        int type, String[] symbols) {
        for (int i = 0; i < symbols.length; i++) {
            String symbol = symbols[i];
            int position = widgetItemPosition + i;
            String id = mAppWidgetId + "_" + position;

            Setting setting = new Setting();
            setting.setId(id);
            setting.setWidgetId(mAppWidgetId);
            setting.setQuotePosition(position);
            setting.setQuoteType(type);
            setting.setQuoteSymbol(symbol);

            database.settingDao().insertAll(setting);
        }
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
    public List<Model> getModelsByWidgetId(int widgetId) {
        List<Setting> settings = database.settingDao().allByWidgetId(widgetId);
        List<String> symbols = new ArrayList<>(settings.size());

        for (Setting setting: settings) symbols.add(setting.getQuoteSymbol());

        return database.modelDao().allByIds(symbols);
    }

    @Override
    public List<ModelSetting> getSettingsWithModelByWidgetId(int widgetId) {
        List<ModelSetting> modelSettings = new ArrayList<>();
        List<Model> models = getModelsByWidgetId(widgetId);
        if (models.isEmpty()) return modelSettings;

        List<Setting> settings = database.settingDao().allByWidgetId(widgetId);
        Map<String, Model> symbolModel = new HashMap<>();

        for (Model model: models) {
            symbolModel.put(model.getId(), model);
        }

        for (Setting setting: settings) {
            modelSettings.add(new ModelSetting(
                    setting, symbolModel.get(setting.getQuoteSymbol())));
        }

        for (ModelSetting modelSetting: modelSettings) LOGD(TAG, modelSetting.toString());

        return modelSettings;
    }

    @Override
    public void deleteQuotesByIds(String[] symbols) {
        List<Quote> quotes = database.quoteDao().getAllByWidgetId(symbols);
        database.quoteDao().deleteAll(quotes.toArray(new Quote[quotes.size()]));
    }

    @Override
    public List<Quote> getQuotesByType(int quoteType) {
        return database.quoteDao().getAllByQuoteType(quoteType);
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
