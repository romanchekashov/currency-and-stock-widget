package ru.besttuts.stockwidget.ui.fragments.quoteprovider;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.besttuts.stockwidget.provider.model.QuoteProvider;

public class QuoteProviderAdapter extends BaseAdapter {

    Context context;
    List<QuoteProvider> data;
    Set<String> providerCodes = new HashSet<>();
    LayoutInflater lInflater;
    int layout;
    QuoteProviderFragment fragment;

    public QuoteProviderAdapter(Context context, int layout, List<QuoteProvider> data) {
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

    public void setData(List<QuoteProvider> data) {
        this.data = data;
    }

    public void setProviderCodes(Set<String> providerCodes) {
        this.providerCodes = providerCodes;
    }

    public void setFragment(QuoteProviderFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(layout, parent, false);
        }

        QuoteProvider quoteProvider = data.get(position);
//        String providerCode = String.valueOf(((TextView) view.findViewById(android.R.id.text2)).getText());
        if (providerCodes.contains(quoteProvider.getCode())) {
            fragment.setSelectedBgView(view);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

//        String name = Utils.getModelNameFromResourcesBySymbol(context, quoteType, symbol);
//        if (!symbol.equals(name)) {
//            ((TextView) view.findViewById(android.R.id.text1)).setText(name);
//        }
        TextView text1 = view.findViewById(android.R.id.text1);
        text1.setText(quoteProvider.getName());

        return view;
    }
}
