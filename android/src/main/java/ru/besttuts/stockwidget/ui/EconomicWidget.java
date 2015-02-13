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
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

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

    public static final String ARG_HAS_INTERNET = "hasInternet";

    private static final String UPDATE_ALL_WIDGETS = "update_all_widgets";

    public final static String BROADCAST_ACTION = "ru.besttuts.stockwidget";

    private static int widgetLayoutId = R.layout.economic_widget;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        onUpdate(context, appWidgetManager, appWidgetIds, isSyncAllowed(context, false));

    }

    private void onUpdate(Context context, AppWidgetManager appWidgetManager,
                          int[] appWidgetIds, boolean hasInternet) {
        // To prevent any ANR timeouts, we perform the update in a service
        // Получаем все идентификаторы
        ComponentName thisWidget = new ComponentName(context,
                EconomicWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        if (!hasInternet && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
//            Toast.makeText(context, "Wi-Fi disabled!", Toast.LENGTH_SHORT).show();

            // Возможно активны несколько виджетов, поэтому обновляем их все
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                EconomicWidget.updateAppWidget(context, appWidgetManager,
                        appWidgetIds[i], null, hasInternet);
            }
            return;
        }

        update(context, appWidgetManager, allWidgetIds, hasInternet);

        if(BuildConfig.DEBUG) {
            LOGD(TAG, "onUpdate");
            for (int i = 0; i < allWidgetIds.length; i++) {
                LOGD(TAG, "widgetId = " + allWidgetIds[i]);
            }
        }
    }

    private void update(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,
                        boolean hasInternet) {

        // Создаем intent для вызова сервиса
        Intent intent = new Intent(context.getApplicationContext(),
                UpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.putExtra(ARG_HAS_INTERNET, hasInternet);

        // Обновляем виджеты через сервис
        context.startService(intent);

        RemoteViews views = new RemoteViews(context.getPackageName(), widgetLayoutId);
        views.setViewVisibility(R.id.ibRefresh, View.GONE);
        views.setViewVisibility(R.id.progressBar, View.VISIBLE);

        // Возможно активны несколько виджетов, поэтому обновляем их все
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
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

    public static void setAlarm(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        long interval = Integer.parseInt(sharedPreferences.getString(
                ConfigPreferenceFragment.KEY_PREF_UPDATE_INTERVAL,
                ConfigPreferenceFragment.KEY_PREF_UPDATE_INTERVAL_DEFAULT_VALUE));

        if (0 == interval) {
            cancelAlarm(context);
            return;
        }

        // Enter relevant functionality for when the first widget is created
        Intent intent = new Intent(context, EconomicWidget.class);
        intent.setAction(UPDATE_ALL_WIDGETS);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        // TODO: Добавить изменение интервала оповещения при изменении в настройках!!!
        alarmManager.setRepeating(AlarmManager.RTC,
                System.currentTimeMillis(), interval, pIntent);

        LOGD(TAG, String.format("setAlarm: alarmManager(%s), interval = %d",
                alarmManager, interval));

    }

    public static void cancelAlarm(Context context) {

        Intent intent = new Intent(context, EconomicWidget.class);
        intent.setAction(UPDATE_ALL_WIDGETS);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        if (alarmManager!= null) {
            alarmManager.cancel(pIntent);
        }

        LOGD(TAG, String.format("cancelAlarm: alarmManager(%s)", alarmManager));
    }

    /**
     * Вызывается при создании первого экземпляра виджета.
     * @param context
     */
    @Override
    public void onEnabled(Context context) {

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        // включаем слушатель события перезагрузки системы
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        setAlarm(context);

        LOGD(TAG, "onEnabled");

    }

    /**
     * Вызывается при удалении последнего экземпляра виджета.
     * @param context
     */
    @Override
    public void onDisabled(Context context) {

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        // выключаем слушатель события перезагрузки системы
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        cancelAlarm(context);

        LOGD(TAG, "onDisabled");

    }

    /**
     * Обновляем отображение и данные виджета
     * @param context
     * @param appWidgetManager
     * @param appWidgetId идентификатор виджета
     * @param models новые данные
     * @param hasInternet
     */
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, List<Model> models, boolean hasInternet) {

        LOGD(TAG, "updateAppWidget: minHeight = " + appWidgetManager.getAppWidgetInfo(appWidgetId).minHeight);
        LOGD(TAG, "updateAppWidget: minWidth = " + appWidgetManager.getAppWidgetInfo(appWidgetId).minWidth);

        if (null == models) models = new ArrayList<>();

        // Создаем объект RemoteViews для взаимодействи с отображением виджета
        RemoteViews views = new RemoteViews(context.getPackageName(), widgetLayoutId);

        readPrefsSettings(context, views); // считываем настройки виджета

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setGrid(views, context, appWidgetId);
        } else {
            setWidgetViewForApi10(views, context, models);
        }

        if (hasInternet && null == connectionStatus) { // TODO connectionStatus always null!!! Need fix(correction)
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yy");
            String time = dateFormat.format(Calendar.getInstance().getTime());
            views.setTextViewText(R.id.tvSyncTime, time);
            views.setTextColor(R.id.tvSyncTime, Color.WHITE);
            EconomicWidgetConfigureActivity.saveLastUpdateTimePref(context, appWidgetId, time);
        } else {
            String time = EconomicWidgetConfigureActivity.loadLastUpdateTimePref(context, appWidgetId);
            views.setTextViewText(R.id.tvSyncTime, time + " - " + connectionStatus);
            int color = context.getResources().getColor(R.color.arrow_red);
            views.setTextColor(R.id.tvSyncTime, color);
        }

        setConfigBtn(context, appWidgetId, views);
        setRefreshBtn(context, appWidgetId, views);

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

    private static void setRefreshBtn(Context context, int appWidgetId, RemoteViews views) {

        // Обновление виджета (вторая зона)
        Intent updateIntent = new Intent(context, EconomicWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[] { appWidgetId });
        PendingIntent pIntent = PendingIntent.getBroadcast(context, appWidgetId, updateIntent, 0);
        views.setOnClickPendingIntent(R.id.ibRefresh, pIntent);
    }

    private static void setConfigBtn(Context context, int appWidgetId, RemoteViews views) {

        // Конфигурационный экран (первая зона)
        Intent configIntent = new Intent(context, EconomicWidgetConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId,
                configIntent, 0);
        views.setOnClickPendingIntent(R.id.ibSettings, pIntent);
    }

    private static void readPrefsSettings(Context context, RemoteViews views) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String bgColor = "#" + sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_VISIBILITY,
                ConfigPreferenceFragment.KEY_PREF_BG_VISIBILITY_DEFAULT_VALUE) +
                sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                        ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE).substring(1);
        views.setInt(R.id.bgWidget, "setBackgroundColor", Color.parseColor(bgColor));
    }

    @SuppressLint("NewApi")
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

            int color = context.getResources().getColor(R.color.arrow_green);
            if (0 < model.getChange()) {
                viewItem.setImageViewResource(R.id.imageView, R.drawable.ic_widget_green_arrow_up);
            } else {
                viewItem.setImageViewResource(R.id.imageView, R.drawable.ic_widget_green_arrow_down);
                color = context.getResources().getColor(R.color.arrow_red);
            }

            viewItem.setTextColor(R.id.tvChange, color);
            viewItem.setTextColor(R.id.tvChangePercentage, color);

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
        LOGD(TAG, "onReceive"); //com.sec.android.widgetapp.APPWIDGET_RESIZE
        if (intent.getAction().contentEquals("com.sec.android.widgetapp.APPWIDGET_RESIZE")) {
            handleResize(context, intent);
            return;
        }
        if (!intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) return;

        ComponentName thisAppWidget = new ComponentName(
                context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

        onUpdate(context, appWidgetManager, appWidgetIds, isSyncAllowed(context));

        LOGD(TAG, "onReceive: UPDATE_ALL_WIDGETS = " + UPDATE_ALL_WIDGETS);

    }

    private void handleResize(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle newOptions = getOptions(context, intent);
        int appWidgetId = intent.getIntExtra("widgetId", 0);

        if (!newOptions.isEmpty()) {
            onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        }
    }

    public Bundle getOptions(Context context, Intent intent) {
        Bundle newOptions = new Bundle();

        int appWidgetId = intent.getIntExtra("widgetId", 0);
        int widgetSpanX = intent.getIntExtra("widgetspanx", 0);
        int widgetSpanY = intent.getIntExtra("widgetspany", 0);

        if(appWidgetId > 0 && widgetSpanX > 0 && widgetSpanY > 0) {
            newOptions.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, widgetSpanY * 74);
            newOptions.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, widgetSpanX * 74);
        }
        return newOptions;
    }


    public boolean isSyncAllowed(Context context) {
        return isSyncAllowed(context, true);
    }

    public static volatile String connectionStatus;

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
                    if (!wifiConnected) {
                        connectionStatus = context.getString(R.string.connection_status_no_wifi);
                    }
                    return wifiConnected;
                }
            }

            boolean isConnected = wifiConnected || mobileConnected;

            return isConnected;
        }
        connectionStatus = context.getString(R.string.connection_status_no_internet);
        return false;
    }

    @Override
    @SuppressLint("NewApi")
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        int minwidth_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxwidth_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxheight_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        LOGD(TAG, "onAppWidgetOptionsChanged: minwidth_dp = " + minwidth_dp);
        LOGD(TAG, "onAppWidgetOptionsChanged: maxwidth_dp = " + maxwidth_dp);
        LOGD(TAG, "onAppWidgetOptionsChanged: minheight_dp = " + minHeight_dp);
        LOGD(TAG, "onAppWidgetOptionsChanged: maxheight_dp = " + maxheight_dp);

        // First find out rows and columns based on width provided.
        int rows = getCellsForSize(minHeight_dp);
        if(1 < rows) {
            widgetLayoutId = R.layout.economic_widget_row2;
        } else {
            widgetLayoutId = R.layout.economic_widget;
        }

        update(context, appWidgetManager, new int[]{appWidgetId}, false);

    }

    /**
     * Returns number of cells needed for given size of the widget.
     *
     * @param size Widget size in dp.
     * @return Size in number of cells.
     */
    private static int getCellsForSize(int size) {
        int n = 2;
        while (70 * n - 30 < size) {
            ++n;
        }
        return n - 1;
    }

}


