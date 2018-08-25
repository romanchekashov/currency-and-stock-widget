package ru.besttuts.stockwidget.ui.fragments.tracking;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class TrackingQuotesAdapter extends SimpleAdapter {
    public TrackingQuotesAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
