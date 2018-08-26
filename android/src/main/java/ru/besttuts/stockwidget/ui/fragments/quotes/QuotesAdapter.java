package ru.besttuts.stockwidget.ui.fragments.quotes;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.besttuts.stockwidget.provider.model.Quote;
import ru.besttuts.stockwidget.util.Utils;

public class QuotesAdapter extends BaseAdapter {

    Context context;
    List<Quote> data;

    public QuotesAdapter(Context context, int layout, List<Quote> data) {
        this.context = context;
        this.data = data;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        String symbol = String.valueOf(((TextView) view.findViewById(android.R.id.text2)).getText());
        if (mSymbols.contains(symbol)) {
            setSelectedBgView(view);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        String name = Utils.getModelNameFromResourcesBySymbol(context, mQuoteType, symbol);
        if (!symbol.equals(name)) {
            ((TextView) view.findViewById(android.R.id.text1)).setText(name);
        }

        return view;
    }
}
