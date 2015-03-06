package ru.besttuts.stockwidget.provider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.ui.EconomicWidget;
import ru.besttuts.stockwidget.ui.EconomicWidgetConfigureActivity;
import ru.besttuts.stockwidget.util.Utils;

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
    private int mLayout = R.layout.economic_widget_item_row_4;

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
        mLayout = EconomicWidgetConfigureActivity.loadWidgetLayoutGridItemPref(mContext, mAppWidgetId);

        if (null == models) models = new ArrayList<>();
        models.clear();

        QuoteDataSource dataSource = new QuoteDataSource(mContext);

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
        RemoteViews viewItem = new RemoteViews(mContext.getPackageName(), mLayout);
//        viewItem.setInt(R.id.gridItemInnerLinearLayout, "setBackgroundColor",
//                Color.BLACK);
        Model model = models.get(position);

//        viewItem.setFloat(R.id.tvName, "setTextSize", 12);
        viewItem.setTextViewText(R.id.tvName, Utils.getModelNameFromResourcesBySymbol(mContext, model));
        viewItem.setTextViewText(R.id.tvRate, model.getRateToString());
        viewItem.setTextViewText(R.id.tvChange, model.getChangeToString());
        viewItem.setTextViewText(R.id.tvChangePercentage, model.getPercentChange());

        int color = mContext.getResources().getColor(R.color.arrow_green);
        if (0 < model.getChange()) {
            viewItem.setImageViewResource(R.id.imageView, R.drawable.ic_widget_green_arrow_up);
        } else {
            viewItem.setImageViewResource(R.id.imageView, R.drawable.ic_widget_green_arrow_down);
            color = mContext.getResources().getColor(R.color.arrow_red);
        }
        viewItem.setTextColor(R.id.tvChange, color);
        viewItem.setTextColor(R.id.tvChangePercentage, color);

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
