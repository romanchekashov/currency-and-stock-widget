package ru.besttuts.stockwidget.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import ru.besttuts.stockwidget.BuildConfig;
import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.service.QuoteWidgetService;
import ru.besttuts.stockwidget.service.UpdateService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link EconomicWidgetConfigureActivity EconomicWidgetConfigureActivity}
 */
public class EconomicWidget extends AppWidgetProvider {

    private static final String TAG = makeLogTag(EconomicWidget.class);

    public final static String BROADCAST_ACTION = "ru.besttuts.stockwidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // To prevent any ANR timeouts, we perform the update in a service
        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                EconomicWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(),
                UpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Update the widgets via the service
        context.startService(intent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.economic_widget);
        views.setViewVisibility(R.id.ibRefresh, View.GONE);
        views.setViewVisibility(R.id.progressBar, View.VISIBLE);
        for (int i = 0; i < allWidgetIds.length; i++) {
            appWidgetManager.updateAppWidget(allWidgetIds[i], views);
        }

        LOGD(TAG, "onUpdate");
        if(BuildConfig.DEBUG) {
            for (int i = 0; i < allWidgetIds.length; i++) {
                LOGD(TAG, "widgetId = " + allWidgetIds[i]);
            }
        }

//        // There may be multiple widgets active, so update all of them
//        final int N = appWidgetIds.length;
//        for (int i = 0; i < N; i++) {
//            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
//        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            EconomicWidgetConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
        }

        LOGD(TAG, "onUpdate");
        if(BuildConfig.DEBUG) {
            for (int i = 0; i < appWidgetIds.length; i++) {
                LOGD(TAG, "widgetId = " + appWidgetIds[i]);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, List<Model> models) {

        LOGD(TAG, "updateAppWidget: minHeight = " + appWidgetManager.getAppWidgetInfo(appWidgetId).minHeight);
        LOGD(TAG, "updateAppWidget: minWidth = " + appWidgetManager.getAppWidgetInfo(appWidgetId).minWidth);

        if (null == models) models = new ArrayList<>();

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.economic_widget);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setGrid(views, context, appWidgetId);
        } else {
            setWidgetViewForApi10(views, context, models);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yy");
        views.setTextViewText(R.id.tvSyncTime, dateFormat.format(Calendar.getInstance().getTime()));

        // Конфигурационный экран (первая зона)
        Intent configIntent = new Intent(context, EconomicWidgetConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId,
                configIntent, 0);
        views.setOnClickPendingIntent(R.id.ibSettings, pIntent);

        // Обновление виджета (вторая зона)
        Intent updateIntent = new Intent(context, EconomicWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[] { appWidgetId });
        pIntent = PendingIntent.getBroadcast(context, appWidgetId, updateIntent, 0);
        views.setOnClickPendingIntent(R.id.ibRefresh, pIntent);

        views.setViewVisibility(R.id.progressBar, View.GONE);
        views.setViewVisibility(R.id.ibRefresh, View.VISIBLE);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.gridView2);
        }
        LOGD(TAG, "updateAppWidget: appWidgetId = " + appWidgetId);

    }

    private static void setGrid(RemoteViews rv, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, QuoteWidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        // создаем разные Intent-ы
        Uri data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME));
        adapter.setData(data);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            rv.setRemoteAdapter(R.id.gridView2, adapter);
        } else if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB) {
            rv.setRemoteAdapter(appWidgetId, R.id.gridView2, adapter);
        }
    }

    private static void setWidgetViewForApi10(RemoteViews views, Context context, List<Model> models) {
        int i = 0;
        int viewId = R.id.currency;
        views.removeAllViews(viewId);
        for (Model model: models) {
            RemoteViews viewItem = new RemoteViews(context.getPackageName(), R.layout.economic_widget_item);
            viewItem.setTextViewText(R.id.tvName, model.getName());
            viewItem.setTextViewText(R.id.tvRate, model.getRateToString());
            viewItem.setTextViewText(R.id.tvChange, model.getChangeToString());
            viewItem.setTextViewText(R.id.tvChangePercentage, model.getPercentChange());

            if (0 < model.getChange()) {
                viewItem.setImageViewResource(R.id.imageView, R.drawable.ic_widget_green_arrow_up);
                viewItem.setTextColor(R.id.tvChange, Color.parseColor("#00ff00"));
                viewItem.setTextColor(R.id.tvChangePercentage, Color.parseColor("#00ff00"));
            } else {
                viewItem.setImageViewResource(R.id.imageView, R.drawable.ic_widget_green_arrow_down);
                viewItem.setTextColor(R.id.tvChange, Color.parseColor("#ff2a2a"));
                viewItem.setTextColor(R.id.tvChangePercentage, Color.parseColor("#ff2a2a"));
            }
            views.addView(viewId, viewItem);
            i++;
            if (3 == i) {
                viewId = R.id.goods;
                views.removeAllViews(viewId);
            } else if(7 == i) { // заполнены все 8 ячеек виджета, выходил из цикла
                break;
            }

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        LOGD(TAG, "onReceive");

    }

    @Override
    @SuppressLint("NewApi")
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        int minwidth_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxwidth_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minheight_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxheight_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        LOGD(TAG, "onAppWidgetOptionsChanged: minwidth_dp = " + minwidth_dp);
        LOGD(TAG, "onAppWidgetOptionsChanged: maxwidth_dp = " + maxwidth_dp);
        LOGD(TAG, "onAppWidgetOptionsChanged: minheight_dp = " + minheight_dp);
        LOGD(TAG, "onAppWidgetOptionsChanged: maxheight_dp = " + maxheight_dp);

    }
}


