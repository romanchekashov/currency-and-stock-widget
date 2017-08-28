package ru.besttuts.stockwidget.service.tasks;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.service.FetchStockData;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;
import ru.besttuts.stockwidget.ui.EconomicWidget;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 *         created on 27.08.2017
 */

public class FetchStockDataAsyncTask extends AsyncTask<Void, Void, Map<Integer, List<Model>>> {
    private static final String TAG = makeLogTag(FetchStockDataAsyncTask.class);

    private final Service mService;
    private final Context mContext;
    private final AppWidgetManager appWidgetManager;
    private final int[] allWidgetIds;
    private final int startId;
    private final boolean hasInternet;

    public FetchStockDataAsyncTask(Service service, AppWidgetManager appWidgetManager,
                                   int[] allWidgetIds, int startId, boolean hasInternet) {
        this.mService = service;
        this.mContext = service.getApplicationContext();
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
        } catch (Exception e) {
            LOGE(TAG, "" + e.getMessage());
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

        boolean serviceStopped = (null != mService) && mService.stopSelfResult(startId);
        LOGD(TAG, "Load Yahoo Finance Thread#" + startId + " end, can be stopped: " + serviceStopped);
        LOGD(TAG, "onPostExecute: Current thread: " + Thread.currentThread().getName());
    }
}
