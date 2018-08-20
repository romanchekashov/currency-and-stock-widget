package ru.besttuts.stockwidget.service;

import android.app.IntentService;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.service.tasks.FetchStockDataAsyncTask;
import ru.besttuts.stockwidget.sync.deserializer.YahooMultiQueryDataDeserializer;
import ru.besttuts.stockwidget.sync.model.YahooMultiQueryData;
import ru.besttuts.stockwidget.ui.EconomicWidget;
import ru.besttuts.stockwidget.util.CustomConverter;
import ru.besttuts.stockwidget.util.YahooQueryBuilder;

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
        LOGD(TAG, "onStartCommand");

//        LOGD(TAG, "intent=" + intent + ", action=" + intent.getAction() + ", flags=" + flags + " bits=" + Integer.toBinaryString(flags));
        if (null == intent) {
            String source = null == intent ? "intent" : "action";
            LOGE(TAG, source + " was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags));
            return START_NOT_STICKY;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        final int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        boolean hasInternet = intent.getBooleanExtra(EconomicWidget.ARG_HAS_INTERNET, true);

//        new FetchStockDataAsyncTask(this, appWidgetManager, allWidgetIds, startId, hasInternet).execute();

        updateData(this, appWidgetManager, allWidgetIds, -1, hasInternet);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LOGD(TAG, "onDestroy: Current thread: " + Thread.currentThread().getName());
    }

    private void updateData(final Service service, final AppWidgetManager appWidgetManager,
                            final int[] allWidgetIds, final int startId, final boolean hasInternet) {
        final List<Setting> settings = DbProvider.getInstance().getAllSettingsWithCheck();
        String url = YahooQueryBuilder.buildYahooFinanceMultiQueryUrl(settings);
        final Gson gson = new GsonBuilder().registerTypeAdapter(YahooMultiQueryData.class,
                new YahooMultiQueryDataDeserializer()).create();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LOGD(TAG, "Response is: "+ response.substring(0,500));
                        YahooMultiQueryData yahooMultiQueryData = gson.fromJson(response, YahooMultiQueryData.class);
                        Map<Integer, List<Model>> map = map(settings, yahooMultiQueryData);
                        updateWidget(map, service, appWidgetManager, allWidgetIds, startId, hasInternet);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LOGE(TAG, "That didn't work!");
            }
        });
        requestQueue.add(stringRequest);
    }

    private Map<Integer, List<Model>> map(List<Setting> settings, YahooMultiQueryData yahooMultiQueryData){
        Map<Integer, List<Model>> map = new HashMap<>();
        DbProvider dbProvider = DbProvider.getInstance();
        Map<String, Model> symbolModelMap = CustomConverter.convertToModelMap(yahooMultiQueryData);

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

    private void updateWidget(Map<Integer, List<Model>> map, Service service, AppWidgetManager appWidgetManager,
                              int[] allWidgetIds, int startId, boolean hasInternet){

        for (int widgetId: allWidgetIds) {
            EconomicWidget.updateAppWidget(service.getApplicationContext(), appWidgetManager, widgetId,
                    map.get(widgetId), hasInternet);
        }

//        boolean serviceStopped = (null != service) && service.stopSelfResult(startId);
//        LOGD(TAG, "Load Yahoo Finance Thread#" + startId + " end, can be stopped: " + serviceStopped);
        LOGD(TAG, "onPostExecute: Current thread: " + Thread.currentThread().getName());
    }
}
