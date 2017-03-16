package ru.besttuts.stockwidget.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;
import ru.besttuts.stockwidget.ui.EconomicWidget;

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

//        LOGD(TAG, "intent=" + intent + ", action=" + intent.getAction() + ", flags=" + flags + " bits=" + Integer.toBinaryString(flags));
        if (null == intent) {
            String source = null == intent ? "intent" : "action";
            LOGE(TAG, source + " was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags));
            return START_NOT_STICKY;
        }

        final Context context = this.getApplicationContext();
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        boolean hasInternet = intent.getBooleanExtra(EconomicWidget.ARG_HAS_INTERNET, true);

        new FetchStockDataAsyncTask(context, appWidgetManager, allWidgetIds, startId, hasInternet).execute();

        return super.onStartCommand(intent, flags, startId);
    }

    private class FetchStockDataAsyncTask extends AsyncTask<Void, Void, Map<Integer, List<Model>>> {

        private final Context mContext;
        private final AppWidgetManager appWidgetManager;
        private final int[] allWidgetIds;
        private final int startId;
        private final boolean hasInternet;

        private FetchStockDataAsyncTask(Context context, AppWidgetManager appWidgetManager,
                                        int[] allWidgetIds, int startId, boolean hasInternet) {
            this.mContext = context;
            this.appWidgetManager = appWidgetManager;
            this.allWidgetIds = allWidgetIds;
            this.startId = startId;
            this.hasInternet = hasInternet;
        }

        @Override
        protected Map<Integer, List<Model>> doInBackground(Void... params) { //TODO: Написать Unit-тесты!!!

            FetchStockData fetchStockData = new FetchStockData(allWidgetIds, hasInternet,
                    new RemoteYahooFinanceDataFetcher(), DbProvider.getInstance());

            try {
                return fetchStockData.fetch();

            } catch (IOException e) {
//                LOGE(TAG, e.getMessage());
                EconomicWidget.connectionStatus =
                        mContext.getString(R.string.connection_status_default_problem);
            }

            return fetchStockData.getCachedData();
        }

        @Override
        protected void onPostExecute(Map<Integer, List<Model>> map) {
            super.onPostExecute(map);

            for (int widgetId: allWidgetIds) {
                EconomicWidget.updateAppWidget(mContext, appWidgetManager, widgetId,
                        map.get(widgetId), hasInternet);
            }
            LOGD(TAG, "Load Yahoo Finance Thread#" + startId + " end, stopSelfResult("
                    + startId + ") = " + stopSelfResult(startId));
            LOGD(TAG, "onPostExecute: Current thread: " + Thread.currentThread().getName());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LOGD(TAG, "onDestroy: Current thread: " + Thread.currentThread().getName());
    }

}
