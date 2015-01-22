package ru.besttuts.stockwidget.provider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 22.01.2015.
 */
@SuppressLint("NewApi")
public class QuoteRemoteViewsFactory implements RemoteViewsFactory {

    private static final String TAG = makeLogTag(QuoteRemoteViewsFactory.class);

    List<Model> models;
    private Context mContext;
    private int mAppWidgetId;

    public QuoteRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        LOGD(TAG, "QuoteRemoteViewsFactory initialized");

    }

    @Override
    public void onCreate() {
        models = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        if (null == models) models = new ArrayList<>();
        models.clear();

        QuoteDataSource dataSource = new QuoteDataSource(mContext);
        dataSource.open();

        models.addAll(dataSource.getModelsByWidgetId(mAppWidgetId));

        dataSource.close();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews viewItem = new RemoteViews(mContext.getPackageName(), R.layout.economic_widget_item);
        Model model = models.get(position);

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
        return viewItem;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
