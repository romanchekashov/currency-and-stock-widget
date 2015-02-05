package ru.besttuts.stockwidget.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.io.HandleJSON;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;
import ru.besttuts.stockwidget.ui.EconomicWidget;
import ru.besttuts.stockwidget.ui.EconomicWidgetConfigureActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class UpdateService extends Service {

    private static final String TAG = makeLogTag(UpdateService.class);

    public UpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        LOGD(TAG, "onStartCommand");

        final Context context = this.getApplicationContext();
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final int[] allWidgetIds = intent
                .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        boolean hasInternet = intent.getBooleanExtra(EconomicWidget.ARG_HAS_INTERNET, true);

        new FetchStockData(context, appWidgetManager, allWidgetIds, startId, hasInternet).execute();

        return super.onStartCommand(intent, flags, startId);
    }

    private class FetchStockData extends AsyncTask<Void, Void, Map<Integer, List<Model>>> {

        private final Context mContext;
        private final AppWidgetManager appWidgetManager;
        private final int[] allWidgetIds;
        private final int startId;
        private final boolean hasInternet;

        private FetchStockData(Context context, AppWidgetManager appWidgetManager,
                               int[] allWidgetIds, int startId, boolean hasInternet) {
            this.mContext = context;
            this.appWidgetManager = appWidgetManager;
            this.allWidgetIds = allWidgetIds;
            this.startId = startId;
            this.hasInternet = hasInternet;
        }

        private Map<Integer, List<Model>> getCachedData() {
            Map<Integer, List<Model>> map = new HashMap<>();
            QuoteDataSource dataSource = new QuoteDataSource(mContext);
            dataSource.open();

            final int N = allWidgetIds.length;
            for (int i = 0; i < N; i++) {
                int appWidgetId = allWidgetIds[i];
                map.put(appWidgetId, dataSource.getModelsByWidgetId(appWidgetId));
            }

            dataSource.close();

            return map;
        }

        @Override
        protected Map<Integer, List<Model>> doInBackground(Void... params) { //TODO: Написать Unit-тесты!!!
            int ln = allWidgetIds.length;
            if (0 >= ln) {
                return new HashMap<>();
            }

            if (!hasInternet) {
                return getCachedData();
            }

            RemoteYahooFinanceDataFetcher dataFetcher = new RemoteYahooFinanceDataFetcher();
            QuoteDataSource dataSource = new QuoteDataSource(mContext);
            dataSource.open();

            List<Setting> settings = dataSource.getAllSettings();

            dataFetcher.populateQuoteSet(settings);
//            String currencyUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('EURUSD'%2C'USDRUB'%2C'EURRUB'%2C'CNYRUB')%3B&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
//            String goodsUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22GCF15.CMX%22%2C%22PLF15.NYM%22%2C%22PAF15.NYM%22%2C%22SIF15.CMX%22%2C%22HGF15.CMX%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

//            List<Model> models = new ArrayList<>();
            HandleJSON handleJSON = new HandleJSON(mContext);
            try {
                handleJSON.readAndParseJSON(dataFetcher.downloadQuotes());

                Map<String, Model> symbolModelMap = handleJSON.getSymbolModelMap();

                Map<Integer, List<Model>> map = new HashMap<>();

                for (Setting setting: settings) {
                    int widgetId = setting.getWidgetId();
                    if (!map.containsKey(widgetId)) {
                        map.put(widgetId, new ArrayList<Model>());
                    }
                    map.get(widgetId).add(symbolModelMap.get(setting.getQuoteSymbol()));
                }

                for (Map.Entry<Integer, List<Model>> me: map.entrySet()) {
                    int widgetId = me.getKey();
                    List<Model> models = me.getValue();

                    for (int i = 0, l = models.size(); i < l; i++) {
                        dataSource.addModelRec(models.get(i));
                    }
                }

//                models.addAll(handleJSON.readAndParseCurrencyJSON(dataFetcher.downloadUrl(currencyUrl)));
//                models.addAll(handleJSON.readAndParseGoodsJSON(dataFetcher.downloadUrl(goodsUrl)));

                // при успешном получении данных, удаляем статус о проблемах соединения
//                final int N = allWidgetIds.length;
//                for (int i = 0; i < N; i++) {
//                    EconomicWidgetConfigureActivity.deleteConnectionStatusPref(mContext,
//                            allWidgetIds[i]);
//                }
                EconomicWidget.connectionStatus = null;

                return map;
            } catch (IOException e) {
                // TODO обработать ошибку и выводить в статус
//                e.printStackTrace();
                LOGE(TAG, e.getMessage());
//                final int N = allWidgetIds.length;
//                for (int i = 0; i < N; i++) {
//                    EconomicWidgetConfigureActivity.saveConnectionStatusPref(mContext,
//                            allWidgetIds[i], "connection problem");
//                }
                EconomicWidget.connectionStatus =
                        mContext.getString(R.string.connection_status_default_problem);
            } finally {
                dataSource.close();
            }

            return getCachedData();
        }

        @Override
        protected void onPostExecute(Map<Integer, List<Model>> map) {
            super.onPostExecute(map);

            // There may be multiple widgets active, so update all of them
            final int N = allWidgetIds.length;
            for (int i = 0; i < N; i++) {
                EconomicWidget.updateAppWidget(mContext, appWidgetManager, allWidgetIds[i],
                        map.get(allWidgetIds[i]), hasInternet);
            }
            LOGD(TAG, "Load Yahoo Finance Thread#" + startId + " end, stopSelfResult("
                    + startId + ") = " + stopSelfResult(startId));
            LOGD(TAG, "onPostExecute: Current thread: " + Thread.currentThread().getName());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LOGD(TAG, "onDestroy: Current thread: " + Thread.currentThread().getName());
    }
}
