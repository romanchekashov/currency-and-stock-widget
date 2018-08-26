package ru.besttuts.stockwidget.ui.fragments.tracking;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import ru.besttuts.stockwidget.provider.model.wrap.ModelSetting;

public class TrackingQuotesAdapter extends BaseAdapter {

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
        return null;
    }
}
