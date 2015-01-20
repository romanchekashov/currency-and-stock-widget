package ru.besttuts.stockwidget.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.service.UpdateService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link EconomicWidgetConfigureActivity EconomicWidgetConfigureActivity}
 */
public class EconomicWidget extends AppWidgetProvider {

    final String LOG_TAG = "EconomicWidget";

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

        Log.d("EconomicWidget.UpdateService", "onUpdate()");

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

        if (null == models) models = new ArrayList<>();

        CharSequence widgetText = EconomicWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.economic_widget);

        int i = 0;
        int viewId = R.id.currency;
        views.removeAllViews(viewId);
        for (Model model: models) {
            RemoteViews viewItem = new RemoteViews(context.getPackageName(), R.layout.economic_widget_item);
            viewItem.setTextViewText(R.id.tvName, model.getName());
            viewItem.setTextViewText(R.id.tvRate, model.getRateToString());
            viewItem.setTextViewText(R.id.tvChange, String.format("%s(%s)",
                    model.getChangeToString(), model.getPercentChange()));

            if (0 < model.getChange()) {
                viewItem.setImageViewResource(R.id.imageView, R.drawable.ic_widget_green_arrow_up);
                viewItem.setTextColor(R.id.tvChange, Color.parseColor("#00ff00"));
            } else {
                viewItem.setImageViewResource(R.id.imageView, R.drawable.ic_widget_green_arrow_down);
                viewItem.setTextColor(R.id.tvChange, Color.parseColor("#ff2a2a"));
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

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

}


