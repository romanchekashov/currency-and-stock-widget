package ru.besttuts.stockwidget.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;

import ru.besttuts.stockwidget.service.tasks.FetchStockDataAsyncTask;
import ru.besttuts.stockwidget.ui.EconomicWidget;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class UpdateService extends Service {

    private static final String TAG = makeLogTag(UpdateService.class);

    public UpdateService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        LOGD(TAG, "onStartCommand");

//        LOGD(TAG, "intent=" + intent + ", action=" + intent.getAction() + ", flags=" + flags + " bits=" + Integer.toBinaryString(flags));
        if (null == intent) {
            String source = null == intent ? "intent" : "action";
            LOGE(TAG, source + " was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags));
            return START_NOT_STICKY;
        }

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        final int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        boolean hasInternet = intent.getBooleanExtra(EconomicWidget.ARG_HAS_INTERNET, true);

        new FetchStockDataAsyncTask(this, appWidgetManager, allWidgetIds, startId, hasInternet).execute();

        return super.onStartCommand(intent, flags, startId);
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
