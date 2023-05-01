package ru.besttuts.stockwidget.service;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.sync.money.dto.TickerConverter;
import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.sync.money.MoneyRemoteService;
import ru.besttuts.stockwidget.sync.money.dto.QuoteDto;
import ru.besttuts.stockwidget.sync.money.dto.TickerFilterDto;
import ru.besttuts.stockwidget.ui.EconomicWidget;

/**
 * @author rchekashov
 *         created on 16.03.2017
 */

public class FetchStockData {
    private static final String TAG = makeLogTag(FetchStockData.class);

    private int[] allWidgetIds;
    private boolean hasInternet;
    private MoneyRemoteService dataFetcher;
    private DbProvider dbProvider;

    public FetchStockData(int[] allWidgetIds, boolean hasInternet, MoneyRemoteService dataFetcher,
                   DbProvider dbProvider) {
        this.allWidgetIds = allWidgetIds;
        this.hasInternet = hasInternet;
        this.dataFetcher = dataFetcher;
        this.dbProvider = dbProvider;
    }

    public Map<Integer, List<Model>> getCachedData() {
        Map<Integer, List<Model>> map = new HashMap<>();

        for (int appWidgetId: allWidgetIds) {
            map.put(appWidgetId, DbProvider.getInstance().getModelsByWidgetId(appWidgetId));
        }

        return map;
    }

    public Map<Integer, List<Model>> fetch() throws IOException {
        Map<Integer, List<Model>> map = new HashMap<>();

        int ln = allWidgetIds.length;
        if (0 >= ln) {
            return map;
        }

        if (!hasInternet) {
            return getCachedData();
        }

        List<Setting> settings = dbProvider.getAllSettingsWithCheck();

        if (0 == settings.size()) return map;

        Map<String, Model> symbolModelMap = getSymbolModelMap(settings);

        for (Setting setting : settings) {
            int widgetId = setting.getWidgetId();
            if (!map.containsKey(widgetId)) {
                map.put(widgetId, new ArrayList<Model>());
            }
            map.get(widgetId).add(symbolModelMap.get(setting.getQuoteSymbol()));
        }

        for (Map.Entry<Integer, List<Model>> me : map.entrySet()) {
            List<Model> models = me.getValue();

            for (int i = 0, l = models.size(); i < l; i++) {
                Model model = models.get(i);
                if (null == model) continue;
                if(!dbProvider.addModelRec(model)){
                    models.set(i, dbProvider.getModelById(model.getId()));
                }
            }
        }

        // при успешном получении данных, удаляем статус о проблемах соединения
        EconomicWidget.connectionStatus = null;

        return map;
    }

    private Map<String, Model> getSymbolModelMap(List<Setting> settings) throws IOException {
        List<String> symbols = new ArrayList<>();
        for (Setting setting: settings) symbols.add(setting.getQuoteSymbol());

        List<QuoteDto> quotes = dataFetcher.tickerTape(new TickerFilterDto().setSymbols(symbols));

        Map<String, Model> symbolModelMap = TickerConverter.convertToModelMap(quotes);

        return symbolModelMap;
    }
}
