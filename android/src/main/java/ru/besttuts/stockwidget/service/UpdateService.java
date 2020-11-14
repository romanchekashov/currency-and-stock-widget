package ru.besttuts.stockwidget.service;

import android.app.IntentService;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Setting;
import ru.besttuts.stockwidget.sync.MyFinanceWS;
import ru.besttuts.stockwidget.sync.sparklab.dto.QuoteDto;
import ru.besttuts.stockwidget.ui.EconomicWidget;
import ru.besttuts.stockwidget.util.CustomConverter;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class UpdateService extends IntentService {

    private static final String TAG = makeLogTag(UpdateService.class);

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (null == intent) {
            LOGE(TAG, intent + " was null, flags=" + flags
                    + " bits=" + Integer.toBinaryString(flags));
            return START_NOT_STICKY;
        }
        LOGD(TAG, "[onStartCommand]intent=" + intent + ", action=" + intent.getAction() + ", flags=" + flags + " bits=" + Integer.toBinaryString(flags));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        final int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        boolean hasInternet = intent.getBooleanExtra(EconomicWidget.ARG_HAS_INTERNET, true);

        updateData(this, appWidgetManager, allWidgetIds, -1, hasInternet);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LOGD(TAG, "onDestroy: Current thread: " + Thread.currentThread().getName());
    }

    private void updateData(final Service service, final AppWidgetManager appWidgetManager,
                            final int[] allWidgetIds, final int startId,
                            final boolean hasInternet) {
        final List<Setting> settings = DbProvider.getInstance().getAllSettingsWithCheck();
        List<String> symbols = new ArrayList<>(settings.size());
        Map<String, List<Integer>> symbolWidgetId = new HashMap<>();
        for (Setting setting: settings) {
            String symbol = setting.getQuoteSymbol();
            symbols.add(symbol);
            if (symbolWidgetId.get(symbol) == null) {
                symbolWidgetId.put(symbol, new ArrayList<Integer>());
            }
            symbolWidgetId.get(symbol).add(setting.getWidgetId());
        }

        try {
            List<QuoteDto> quoteDtos = new MyFinanceWS(this).getQuotes(symbols);
            Map<Integer, List<Model>> modelsByWidgetId = new HashMap<>();
            for (QuoteDto dto: quoteDtos) {
                Model model = CustomConverter.toModel(dto);
                List<Integer> widgetIds = symbolWidgetId.get(dto.getSymbol());
                for (Integer i: widgetIds) {
                    if (modelsByWidgetId.get(i) == null) {
                        modelsByWidgetId.put(i, new ArrayList<Model>());
                    }
                    modelsByWidgetId.get(i).add(model);
                }
            }

            // при успешном получении данных, удаляем статус о проблемах соединения
            EconomicWidget.connectionStatus = null;

            updateWidget(modelsByWidgetId, service, appWidgetManager,
                    allWidgetIds, startId, hasInternet);
        } catch (IOException e) {
            e.printStackTrace();
            LOGE(TAG, "That didn't work!");
        }
    }

    private void updateWidget(Map<Integer, List<Model>> map, Service service, AppWidgetManager appWidgetManager,
                              int[] allWidgetIds, int startId, boolean hasInternet){

        for (int widgetId: allWidgetIds) {
            EconomicWidget.updateAppWidget(service.getApplicationContext(), appWidgetManager,
                    widgetId, map.get(widgetId), hasInternet);
        }

//        boolean serviceStopped = (null != service) && service.stopSelfResult(startId);
//        LOGD(TAG, "Load Yahoo Finance Thread#" + startId + " end, can be stopped: " + serviceStopped);
        LOGD(TAG, "onPostExecute: Current thread: " + Thread.currentThread().getName());
    }
}
