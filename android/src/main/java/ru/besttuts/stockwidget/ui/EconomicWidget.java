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
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import ru.besttuts.stockwidget.BuildConfig;
import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.service.QuoteWidgetService;
import ru.besttuts.stockwidget.service.UpdateService;
import ru.besttuts.stockwidget.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

//        if (!hasInternet && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
//            // Возможно активны несколько виджетов, поэтому обновляем их все
//            final int N = appWidgetIds.length;
//            for (int i = 0; i < N; i++) {
//                EconomicWidget.updateAppWidget(context, appWidgetManager,
//                        appWidgetIds[i], null, hasInternet);
//            }
//            return;
//        }

        LOGD(TAG, String.format("onUpdate: context(%s), appWidgetManager(%s), allWidgetIds(%s), hasInternet(%s)",
                context, appWidgetManager, allWidgetIds, hasInternet));

        update(context, appWidgetManager, allWidgetIds, hasInternet);

        if(BuildConfig.DEBUG) {
            LOGD(TAG, "onUpdate");
            for (int i = 0; i < allWidgetIds.length; i++) {
                LOGD(TAG, "widgetId = " + allWidgetIds[i]);
            }
        }
    }

    private void update(Context context, AppWidgetManager appWidgetManager,
                        int[] appWidgetIds, boolean hasInternet) {

        for (int widgetId: appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    getWidgetLayoutId(context, widgetId));
            views.setViewVisibility(R.id.ibRefresh, View.GONE);
            views.setViewVisibility(R.id.progressBar, View.VISIBLE);
            appWidgetManager.updateAppWidget(widgetId, views);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            for (int widgetId: appWidgetIds) {
//                updateAppWidget(context, appWidgetManager, widgetId, null, hasInternet);
//            }
//        } else {
//        }

        // Создаем intent для вызова сервиса
        Intent intent = new Intent(context.getApplicationContext(),
                UpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.putExtra(ARG_HAS_INTERNET, hasInternet);

        // Обновляем виджеты через сервис
        context.startService(intent);


        // Возможно активны несколько виджетов, поэтому обновляем их все
//        final int N = appWidgetIds.length;
//        for (int i = 0; i < N; i++) {
//            RemoteViews views = new RemoteViews(context.getPackageName(), getWidgetLayoutId(context, appWidgetIds[i]));
//            views.setViewVisibility(R.id.ibRefresh, View.GONE);
//            views.setViewVisibility(R.id.progressBar, View.VISIBLE);
//            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
//        }

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Удаляем все данные ассоциированные с удаляемым виджетом.
        QuoteDataSource dataSource = new QuoteDataSource(context);
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            dataSource.deleteSettingsByWidgetId(appWidgetIds[i]);
            EconomicWidgetConfigureActivity.deleteLastUpdateTimePref(context, appWidgetIds[i]);
            EconomicWidgetConfigureActivity.deleteWidgetLayoutPref(context, appWidgetIds[i]);
            EconomicWidgetConfigureActivity.deleteWidgetLayoutGridItemPref(context, appWidgetIds[i]);
        }
        dataSource.close();

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

        ComponentName thisAppWidget = new ComponentName(
                context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

        LOGD(TAG, "onEnabled: appWidgetId = " + appWidgetIds[0]);

        QuoteDataSource dataSource = new QuoteDataSource(context);

        String lng = context.getResources().getConfiguration().locale.getLanguage();
        LOGD(TAG, "locale.getLanguage: " + lng);

        final String[] defaultCommodities = new String[]{"BZF16.NYM", "NGZ15.NYM", "GCX15.CMX"};
        if ("ru".equals(lng)) {
            dataSource.addSettingsRec(appWidgetIds[0], 1, QuoteType.CURRENCY,
                    new String[]{"EURUSD", "USDRUB", "EURRUB"});
            dataSource.addSettingsRec(appWidgetIds[0], 4, QuoteType.GOODS, defaultCommodities);
        } else if ("uk".equals(lng)) {
            dataSource.addSettingsRec(appWidgetIds[0], 1, QuoteType.CURRENCY,
                    new String[]{"EURUSD", "USDUAH", "EURUAH"});
            dataSource.addSettingsRec(appWidgetIds[0], 4, QuoteType.GOODS, defaultCommodities);
        } else {
            dataSource.addSettingsRec(appWidgetIds[0], 1, QuoteType.CURRENCY,
                    new String[]{"EURUSD"});
            dataSource.addSettingsRec(appWidgetIds[0], 2, QuoteType.GOODS, defaultCommodities);
        }

//        dataSource.addModelRec(QuoteType.CURRENCY, "EURUSD");
//        dataSource.addModelRec(QuoteType.CURRENCY, "USDRUB");
//        dataSource.addModelRec(QuoteType.CURRENCY, "EURRUB");
//
//        dataSource.addModelRec(QuoteType.GOODS, "BZH15.NYM");
//        dataSource.addModelRec(QuoteType.GOODS, "NGH15.NYM");
//        dataSource.addModelRec(QuoteType.GOODS, "GCF15.CMX");
//
//        dataSource.addModelRec(QuoteType.STOCK, "AAPL");
//        dataSource.addModelRec(QuoteType.STOCK, "GOOG");
//        dataSource.addModelRec(QuoteType.STOCK, "IBM");
//        dataSource.addModelRec(QuoteType.STOCK, "MCD");
//        dataSource.addModelRec(QuoteType.STOCK, "MSFT");
//        dataSource.addModelRec(QuoteType.STOCK, "KO");
//        dataSource.addModelRec(QuoteType.STOCK, "T");
//        dataSource.addModelRec(QuoteType.STOCK, "PM");
//        dataSource.addModelRec(QuoteType.STOCK, "CHL");
//        dataSource.addModelRec(QuoteType.STOCK, "GE");
//        dataSource.addModelRec(QuoteType.STOCK, "VOD");
//        dataSource.addModelRec(QuoteType.STOCK, "VZ");
//        dataSource.addModelRec(QuoteType.STOCK, "AMZN");
//        dataSource.addModelRec(QuoteType.STOCK, "WMT");
//        dataSource.addModelRec(QuoteType.STOCK, "WFC");
//        dataSource.addModelRec(QuoteType.STOCK, "UPS");
//        dataSource.addModelRec(QuoteType.STOCK, "HPQ");
//        dataSource.addModelRec(QuoteType.STOCK, "TMUS");
//        dataSource.addModelRec(QuoteType.STOCK, "Visa");

        dataSource.close();

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

        if (null == models) models = new ArrayList<>();
        LOGD(TAG, "updateAppWidget: minHeight = " + appWidgetManager.getAppWidgetInfo(appWidgetId).minHeight);

        // Создаем объект RemoteViews для взаимодействи с отображением виджета
        RemoteViews views = new RemoteViews(context.getPackageName(), getWidgetLayoutId(context, appWidgetId));

        readPrefsSettings(context, views); // считываем настройки виджета

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            calcWidgetViewData(context, appWidgetId, appWidgetManager.getAppWidgetInfo(appWidgetId).minHeight, false);
            setGrid(views, context, appWidgetId);
        } else {
            int columns = getCellsForSize(appWidgetManager.getAppWidgetInfo(appWidgetId).minWidth);
            setWidgetViewForApi10(views, context, models, columns);
        }

        if (hasInternet && null == connectionStatus) {
            String time = new SimpleDateFormat().format(Calendar.getInstance().getTime());
            views.setTextViewText(R.id.tvSyncTime, time);
            views.setTextColor(R.id.tvSyncTime, Color.WHITE);
            EconomicWidgetConfigureActivity.saveLastUpdateTimePref(context, appWidgetId, time);
        } else if(null != connectionStatus) {
            String time = EconomicWidgetConfigureActivity.loadLastUpdateTimePref(context, appWidgetId);
            views.setTextViewText(R.id.tvSyncTime, time + " - " + connectionStatus);
            int color = context.getResources().getColor(R.color.arrow_red);
            views.setTextColor(R.id.tvSyncTime, color);
        } else {
            String time = EconomicWidgetConfigureActivity.loadLastUpdateTimePref(context, appWidgetId);
            views.setTextViewText(R.id.tvSyncTime, time);
            views.setTextColor(R.id.tvSyncTime, Color.WHITE);
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

    // Обновление виджета (вторая зона)
    private static void setRefreshBtn(Context context, int appWidgetId, RemoteViews views) {
        Intent updateIntent = new Intent(context, EconomicWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[] { appWidgetId });
        PendingIntent pIntent = PendingIntent.getBroadcast(context, appWidgetId, updateIntent, 0);
        views.setOnClickPendingIntent(R.id.ibRefresh, pIntent);
    }

    // Конфигурационный экран (первая зона)
    private static void setConfigBtn(Context context, int appWidgetId, RemoteViews views) {
        Intent configIntent = new Intent(context, EconomicWidgetConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId,
                configIntent, 0);
        views.setOnClickPendingIntent(R.id.ibSettings, pIntent);
    }

    private static void readPrefsSettings(Context context, RemoteViews views) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String sVisibility = sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_VISIBILITY,
                ConfigPreferenceFragment.KEY_PREF_BG_VISIBILITY_DEFAULT_VALUE);
        if (2 != sVisibility.length()){
            sVisibility = "0" + sVisibility;
        }
        String bgColor = "#" + sVisibility + sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
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

        Intent intent = new Intent(context, DynamicWebViewActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
        rv.setPendingIntentTemplate(R.id.gridView2, viewPendingIntent);

    }

    private static void setWidgetViewForApi10(RemoteViews views, Context context, List<Model> models, int columns) {
        int i = 0;
        int viewId = R.id.firstLinearLayout;
        views.removeAllViews(viewId);
        for (Model model: models) {
            if (null == model) continue;

            RemoteViews viewItem = new RemoteViews(context.getPackageName(), R.layout.economic_widget_item);
            viewItem.setTextViewText(R.id.tvName, Utils.getModelNameFromResourcesBySymbol(context, model));
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

            if (3 == ++i) { // одну колонку занимают кнопки обновления и настроек
                LOGD(TAG, "setWidgetViewForApi10: columns = " + columns);
                break;
            }

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        LOGD(TAG, "onReceive: intent.getAction = " + action);

        // Отлавливаем событие изменения размера виджета
        if (action.contentEquals("com.sec.android.widgetapp.APPWIDGET_RESIZE")) {
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


    public static volatile String connectionStatus;

    public boolean isSyncAllowed(Context context) {
        return isSyncAllowed(context, true);
    }

    /**
     * Проверяем сетевое соединение.
     *
     * @param context
     * @param isCheckSharedPreferences
     * @return
     */
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
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

//        int minWidth_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
//        int maxWidth_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
//        int maxHeight_dp = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        LOGD(TAG, "onAppWidgetOptionsChanged: minHeight_dp = " + minHeight_dp);

        calcWidgetViewData(context, appWidgetId, minHeight_dp);

        // обновляем виджет с новым layout
        update(context, appWidgetManager, new int[]{appWidgetId}, false);

    }

    /**
     * Возвращает кол-во ячеек необходимое для данного размера виджета.
     *
     * @param size Размер виджета dp.
     * @return Размер в кол-ве ячеек.
     */
    private static int getCellsForSize(int size) {
        int n = 2;
        while (70 * n - 30 < size) {
            ++n;
        }
        return n - 1;
    }

    private static int getWidgetLayoutId(Context context, int appWidgetId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return EconomicWidgetConfigureActivity.loadWidgetLayoutPref(context, appWidgetId);
        } else {
            return R.layout.economic_widget;
        }
    }

    private static void calcWidgetViewData(Context context, int appWidgetId, int height) {

        // Находим кол-во строк по минимальной высоте виджета.
        int rows = getCellsForSize(height);
        LOGD(TAG, "calcWidgetViewData: rows = " + rows);
        switch (rows) {
            case 1:
                EconomicWidgetConfigureActivity.saveWidgetLayoutPref(context, appWidgetId, R.layout.economic_widget);
                EconomicWidgetConfigureActivity.saveWidgetLayoutGridItemPref(context, appWidgetId, R.layout.economic_widget_item);
                break;
            case 2:
                EconomicWidgetConfigureActivity.saveWidgetLayoutPref(context, appWidgetId, R.layout.economic_widget_row2);
                EconomicWidgetConfigureActivity.saveWidgetLayoutGridItemPref(context, appWidgetId, R.layout.economic_widget_item_row2);
                break;
            case 3:
                EconomicWidgetConfigureActivity.saveWidgetLayoutPref(context, appWidgetId, R.layout.economic_widget_row_3);
                EconomicWidgetConfigureActivity.saveWidgetLayoutGridItemPref(context, appWidgetId, R.layout.economic_widget_item_row_3);
                break;
            default:
                EconomicWidgetConfigureActivity.saveWidgetLayoutPref(context, appWidgetId, R.layout.economic_widget_row_4);
                EconomicWidgetConfigureActivity.saveWidgetLayoutGridItemPref(context, appWidgetId, R.layout.economic_widget_item_row_4);
                break;
        }

    }

}


