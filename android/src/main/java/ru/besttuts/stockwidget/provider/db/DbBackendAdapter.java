package ru.besttuts.stockwidget.provider.db;

import java.util.List;

import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.QuoteProvider;
import ru.besttuts.stockwidget.provider.model.Setting;
import ru.besttuts.stockwidget.provider.model.wrap.ModelSetting;

public interface DbBackendAdapter {
    void deleteAll();

    List<Setting> getAllSettings();

    List<Setting> getSettingsByWidgetId(int widgetId);

    void deleteSettingsByWidgetId(int widgetId);

    void deleteSettingsByIdAndUpdatePositions(String settingId, int position);

    Model getModelById(String modelId);


    void deleteQuotesByIds(String[] symbols);

    List<QuoteProvider> getQuoteProviders();
}
