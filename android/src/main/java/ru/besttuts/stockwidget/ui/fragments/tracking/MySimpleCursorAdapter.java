package ru.besttuts.stockwidget.ui.fragments.tracking;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cursoradapter.widget.SimpleCursorAdapter;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

@Deprecated
public class MySimpleCursorAdapter extends SimpleCursorAdapter {
    private static final String TAG = makeLogTag(MySimpleCursorAdapter.class);
    private Context context;

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

    MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.configure_quote_grid_item, parent, false);
            //Now create the ViewHolder
            holder = new ViewHolder();
            holder.progressBar = (ProgressBar) row.findViewById(R.id.progressBar2);
            holder.linearLayout = (LinearLayout) row.findViewById(R.id.lLayoutRate);
            holder.quoteName = (TextView) row.findViewById(R.id.quoteName);
            holder.tvRate = (TextView) row.findViewById(R.id.tvRate);
            holder.tvChange = (TextView) row.findViewById(R.id.tvChange);
            holder.tvChangePercentage = (TextView) row.findViewById(R.id.tvChangePercentage);
            holder.tvCurrency = (TextView) row.findViewById(R.id.tvCurrency);
            holder.tvPosition = (TextView) row.findViewById(R.id.tvPosition);
            holder.imageView = (ImageView) row.findViewById(R.id.imageView);
            //and store it as the 'tag' of our view
            row.setTag(holder);
        } else {
            //We've already seen this one before!
            holder = (ViewHolder) row.getTag();
        }

        Cursor cursor = (Cursor) getItem(position);
        String symbol = "";

        if (null == symbol || symbol.isEmpty()) {
            int quoteType = 0;
            symbol = "";
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

        String quotePosition = "0";
        holder.tvPosition.setText(quotePosition);

        LOGD(TAG, "getView: symbol = " + symbol + " quotePosition = " + quotePosition);

        Model model = new Model();
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
        return row;
    }
}
