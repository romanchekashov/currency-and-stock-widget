package ru.besttuts.stockwidget.ui;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import ru.besttuts.stockwidget.BuildConfig;
import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
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

    private static final String UPDATE_ALL_WIDGETS = "update_all_widgets";

    public final static String BROADCAST_ACTION = "ru.besttuts.stockwidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // To prevent any ANR timeouts, we perform the update in a service
        // Получаем все идентификаторы
        ComponentName thisWidget = new ComponentName(context,
                EconomicWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Создаем intent для вызова сервиса
        Intent intent = new Intent(context.getApplicationContext(),
                UpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Обновляем виджеты через сервис
        context.startService(intent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.economic_widget);
        views.setViewVisibility(R.id.ibRefresh, View.GONE);
        views.setViewVisibility(R.id.progressBar, View.VISIBLE);

        // Возможно активны несколько виджетов, поэтому обновляем их все
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            appWidgetManager.updateAppWidget(allWidgetIds[i], views);
        }

        if(BuildConfig.DEBUG) {
            LOGD(TAG, "onUpdate");
            for (int i = 0; i < allWidgetIds.length; i++) {
                LOGD(TAG, "widgetId = " + allWidgetIds[i]);
            }
        }

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Удаляем все данные ассоциированные с удаляемым виджетом.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            QuoteDataSource dataSource = new QuoteDataSource(context);
            dataSource.open();
            dataSource.deleteSettingsByWidgetId(appWidgetIds[i]);
            dataSource.close();
            EconomicWidgetConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
        }

        if(BuildConfig.DEBUG) {
            LOGD(TAG, "onDeleted: ");
            for (int i = 0; i < appWidgetIds.length; i++) {
                LOGD(TAG, "widgetId = " + appWidgetIds[i]);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Intent intent = new Intent(context, EconomicWidget.class);
        intent.setAction(UPDATE_ALL_WIDGETS);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        long interval = Integer.parseInt(sharedPreferences.getString(
                ConfigPreferenceFragment.KEY_PREF_UPDATE_INTERVAL,
                ConfigPreferenceFragment.KEY_PREF_UPDATE_INTERVAL_DEFAULT_VALUE));

        // TODO: Добавить изменение интервала оповещения при изменении в настройках!!!
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pIntent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Intent intent = new Intent(context, EconomicWidget.class);
        intent.setAction(UPDATE_ALL_WIDGETS);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }

    /**
     * Обновляем отображение и данные виджета
     * @param context
     * @param appWidgetManager
     * @param appWidgetId идентификатор виджета
     * @param models новые данные
     */
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, List<Model> models) {

        LOGD(TAG, "updateAppWidget: minHeight = " + appWidgetManager.getAppWidgetInfo(appWidgetId).minHeight);
        LOGD(TAG, "updateAppWidget: minWidth = " + appWidgetManager.getAppWidgetInfo(appWidgetId).minWidth);

        if (null == models) models = new ArrayList<>();

        // Создаем объект RemoteViews для взаимодействи с отображением виджета
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.economic_widget);

        readPrefsSettings(context, views); // считываем настройки виджета

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

        // Оповещаем менеджер вижетов о необходимости обновить виджет
        appWidgetManager.updateAppWidget(appWidgetId, views);

        // для 11-ой и поздней версии оповещаем менеджер виджетов о изменении данных для GridView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.gridView2);
        }

        LOGD(TAG, "updateAppWidget: appWidgetId = " + appWidgetId);

    }

    private static void readPrefsSettings(Context context, RemoteViews views) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String bgColor = "#" + sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_VISIBILITY,
                ConfigPreferenceFragment.KEY_PREF_BG_VISIBILITY_DEFAULT_VALUE) +
                sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                        ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE).substring(1);
        views.setInt(R.id.bgWidget, "setBackgroundColor", Color.parseColor(bgColor));
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
        if (!intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) return;
        if (!isSyncAllowed(context)) return;

        ComponentName thisAppWidget = new ComponentName(
                context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

        onUpdate(context, appWidgetManager, ids);

        LOGD(TAG, "onReceive: UPDATE_ALL_WIDGETS = " + UPDATE_ALL_WIDGETS);

    }

    public boolean isSyncAllowed(Context context) {
        return isSyncAllowed(context, true);
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    public boolean isSyncAllowed(Context context, boolean isCheckSharedPreferences) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            boolean wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            boolean mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;

            if (isCheckSharedPreferences) {
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(context);
                if (ConfigPreferenceFragment.KEY_PREF_UPDATE_VIA_DEFAULT_VALUE_WI_FI
                        .equalsIgnoreCase(sharedPreferences.getString(
                                ConfigPreferenceFragment.KEY_PREF_UPDATE_VIA,
                                ConfigPreferenceFragment.KEY_PREF_UPDATE_VIA_DEFAULT_VALUE_WI_FI))) {
                    return wifiConnected;
                }
            }

            return wifiConnected || mobileConnected;
        }
        return false;
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


