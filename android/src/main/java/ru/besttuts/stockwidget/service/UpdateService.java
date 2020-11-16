package ru.besttuts.stockwidget.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.sync.MyFinanceWS;
import ru.besttuts.stockwidget.sync.sparklab.dto.MobileQuoteShort;
import ru.besttuts.stockwidget.ui.EconomicWidget;
import ru.besttuts.stockwidget.util.CustomConverter;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class UpdateService extends JobIntentService {

    private static final String TAG = makeLogTag(UpdateService.class);

    /**
     * Unique job ID for this service.
     */
    public static final int JOB_ID = 1;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, UpdateService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        final int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        boolean hasInternet = intent.getBooleanExtra(EconomicWidget.ARG_HAS_INTERNET, true);

        updateData(this, appWidgetManager, allWidgetIds, -1, hasInternet);
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
    public void onDestroy() {
        super.onDestroy();

        LOGD(TAG, "onDestroy: Current thread: " + Thread.currentThread().getName());
    }

    private void updateData(final Service service, final AppWidgetManager appWidgetManager,
                            final int[] allWidgetIds, final int startId,
                            final boolean hasInternet) {
        List<Model> models = DbProvider.modelDao().getAll();
        Set<Integer> ids = new HashSet<>(models.size());
        for (Model model : models) ids.add(model.getId());

        try {
            List<MobileQuoteShort> quotes = new MyFinanceWS(this).getQuotes(ids);
            models = new ArrayList<>(quotes.size());
            for (MobileQuoteShort q : quotes) models.add(CustomConverter.toModel(q));

            // при успешном получении данных, удаляем статус о проблемах соединения
            EconomicWidget.connectionStatus = null;

            for (int widgetId : allWidgetIds) {
                EconomicWidget.updateAppWidget(service.getApplicationContext(), appWidgetManager,
                        widgetId, models, hasInternet);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGE(TAG, "That didn't work!");
        }
    }
}
