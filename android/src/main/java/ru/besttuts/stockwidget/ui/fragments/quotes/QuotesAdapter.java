package ru.besttuts.stockwidget.ui.fragments.quotes;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import ru.besttuts.stockwidget.provider.model.Quote;
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class QuotesAdapter extends BaseAdapter {
    private static String TAG = makeLogTag(QuotesAdapter.class);

    Context context;
    List<Quote> data;
    Set<String> symbols;
    LayoutInflater lInflater;
    int layout;
    int quoteType;
    AbsQuoteSelectionFragment fragment;

    public QuotesAdapter(Context context, int layout, List<Quote> data) {
        this.context = context;
        this.data = data;
        this.layout = layout;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public void setData(List<Quote> data) {
        this.data = data;
    }

    public void setSymbols(Set<String> symbols) {
        this.symbols = symbols;
    }

    public void setQuoteType(int quoteType) {
        this.quoteType = quoteType;
    }

    public void setFragment(AbsQuoteSelectionFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = lInflater.inflate(layout, parent, false);

        Quote quote = data.get(position);
//        LOGD(TAG, String.format("%d %s %s", position, quote.getQuoteSymbol(), quote.getQuoteName()));
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
        text1.setText(quote.getQuoteSymbol());
        text2.setText(quote.getQuoteName());
//        String symbol = String.valueOf(text2.getText());
//        if (symbols.contains(symbol)) {
//            fragment.setSelectedBgView(view);
//        } else {
//            view.setBackgroundColor(Color.TRANSPARENT);
//        }
//
//        String name = Utils.getModelNameFromResourcesBySymbol(context, quoteType, symbol);
//        if (!symbol.equals(name)) {
//            ((TextView) view.findViewById(android.R.id.text1)).setText(name);
//        }

        return view;
    }
}
