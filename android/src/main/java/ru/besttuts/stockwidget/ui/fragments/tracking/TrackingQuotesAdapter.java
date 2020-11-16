package ru.besttuts.stockwidget.ui.fragments.tracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.wrap.ModelSetting;
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class TrackingQuotesAdapter extends BaseAdapter {
    private static final String TAG = makeLogTag(TrackingQuotesAdapter.class);

    /**
     * http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    static class ViewHolder {
        ProgressBar progressBar;
        LinearLayout linearLayout;
        TextView quoteName;
        TextView tvRate;
        TextView tvChange;
        TextView tvChangePercentage;
        TextView tvCurrency;
        TextView tvPosition;
        ImageView imageView;
    }

    Context context;
    List<ModelSetting> data;

    public TrackingQuotesAdapter(Context context, int layout, List<ModelSetting> models) {
        this.context = context;
        this.data = models;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<ModelSetting> data) {
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.configure_quote_grid_item, parent, false);
            //Now create the ViewHolder
            holder = new ViewHolder();
            holder.progressBar = row.findViewById(R.id.progressBar2);
            holder.linearLayout = row.findViewById(R.id.lLayoutRate);
            holder.quoteName =  row.findViewById(R.id.quoteName);
            holder.tvRate =  row.findViewById(R.id.tvRate);
            holder.tvChange =  row.findViewById(R.id.tvChange);
            holder.tvChangePercentage = row.findViewById(R.id.tvChangePercentage);
            holder.tvCurrency = row.findViewById(R.id.tvCurrency);
            holder.tvPosition = row.findViewById(R.id.tvPosition);
            holder.imageView = row.findViewById(R.id.imageView);
            //and store it as the 'tag' of our view
            row.setTag(holder);
        } else {
            //We've already seen this one before!
            holder = (ViewHolder) row.getTag();
        }

        ModelSetting modelSetting = (ModelSetting) getItem(position);
        String symbol = modelSetting.getSetting().getQuoteSymbol();

        if (null == symbol || symbol.isEmpty()) {
            int quoteType = modelSetting.getModel().getQuoteType();
            symbol = modelSetting.getModel().getSymbol();
            holder.quoteName.setText(
                    Utils.getModelNameFromResourcesBySymbol(context, quoteType, symbol));


            holder.linearLayout.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);

//                if (null == mFetchQuote) {
//                    LOGD(TAG, "before FetchQuote: getView: currentThread = " + Thread.currentThread());
//                    LOGD(TAG, String.format("getView: quoteType = %s, symbol = %s", quoteType, symbol));
//
//                    mFetchQuote = (FetchQuote) new FetchQuote(getActivity())
//                            .execute(new String[]{String.valueOf(quoteType), symbol});
//                }

            return row;
        }

        holder.progressBar.setVisibility(View.GONE);
        holder.linearLayout.setVisibility(View.VISIBLE);

        String quotePosition = String.valueOf(modelSetting.getSetting().getQuotePosition());
        holder.tvPosition.setText(quotePosition);

        LOGD(TAG, "getView: symbol = " + symbol + " quotePosition = " + quotePosition);

        Model model = modelSetting.getModel();
        if (model != null) {
            holder.quoteName.setText(Utils.getModelNameFromResourcesBySymbol(context, model));
            holder.tvRate.setText(model.getRateToString());
            holder.tvChange.setText(model.getChangeToString());
            holder.tvChangePercentage.setText(model.getPercentChange());

            int color = context.getResources().getColor(R.color.arrow_green);
            if (0 < model.getChange()) {
                holder.imageView.setImageResource(R.drawable.ic_widget_green_arrow_up);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_widget_green_arrow_down);
                color = context.getResources().getColor(R.color.arrow_red);
            }
            holder.tvChange.setTextColor(color);
            holder.tvChangePercentage.setTextColor(color);

            holder.tvCurrency.setText(model.getCurrency());
        }
        return row;
    }
}
