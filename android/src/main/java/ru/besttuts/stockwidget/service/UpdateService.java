package ru.besttuts.stockwidget.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import ru.besttuts.stockwidget.io.HandleJSON;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;
import ru.besttuts.stockwidget.ui.EconomicWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateService extends Service {

    final String LOG_TAG = "EconomicWidget.UpdateService";

    public UpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Log.d(LOG_TAG, "onStartCommand");

        final Context context = this.getApplicationContext();
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final int[] allWidgetIds = intent
                .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new FetchStockData(context, appWidgetManager, allWidgetIds, startId).execute();
        } else {
            Toast.makeText(context, "No network connection available.", Toast.LENGTH_LONG);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private class FetchStockData extends AsyncTask<Void, Void, Map<Integer, List<Model>>> {

        private final Context context;
        private final AppWidgetManager appWidgetManager;
        private final int[] allWidgetIds;
        private final int startId;

        private FetchStockData(Context context, AppWidgetManager appWidgetManager,
                               int[] allWidgetIds, int startId) {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            this.allWidgetIds = allWidgetIds;
            this.startId = startId;
        }

        @Override
        protected Map<Integer, List<Model>> doInBackground(Void... params) { //TODO: Написать Unit-тесты!!!
            int ln = allWidgetIds.length;
            if (0 >= ln) {
                return new HashMap<>();
            }

            RemoteYahooFinanceDataFetcher dataFetcher = new RemoteYahooFinanceDataFetcher();
            QuoteDataSource dataSource = new QuoteDataSource(context);
            dataSource.open();

            List<Setting> settings = dataSource.getAllSettings();

            dataFetcher.populateQuoteSet(settings);
//            String currencyUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('EURUSD'%2C'USDRUB'%2C'EURRUB'%2C'CNYRUB')%3B&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
//            String goodsUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22GCF15.CMX%22%2C%22PLF15.NYM%22%2C%22PAF15.NYM%22%2C%22SIF15.CMX%22%2C%22HGF15.CMX%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

//            List<Model> models = new ArrayList<>();
            HandleJSON handleJSON = new HandleJSON();
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

                return map;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                dataSource.close();
            }

            return new HashMap<>();
        }

        @Override
        protected void onPostExecute(Map<Integer, List<Model>> map) {
            super.onPostExecute(map);

            // There may be multiple widgets active, so update all of them
            final int N = allWidgetIds.length;
            for (int i = 0; i < N; i++) {
                EconomicWidget.updateAppWidget(context, appWidgetManager, allWidgetIds[i], map.get(allWidgetIds[i]));
            }
            Log.d(LOG_TAG, "Load Yahoo Finance Thread#" + startId + " end, stopSelfResult("
                    + startId + ") = " + stopSelfResult(startId));
            Log.d(LOG_TAG, "onPostExecute: Current thread: " + Thread.currentThread().getName());
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

        Log.d(LOG_TAG, "onDestroy: Current thread: " + Thread.currentThread().getName());
    }
}
