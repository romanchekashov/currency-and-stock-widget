package ru.besttuts.stockwidget.ui.fragments.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import ru.besttuts.stockwidget.provider.model.Notification;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class NotificationsAdapter extends BaseAdapter {
    private static String TAG = makeLogTag(NotificationsAdapter.class);
    private static DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");

    Context context;
    List<Notification> data;
    Set<String> symbols;
    LayoutInflater lInflater;
    int layout;
    int quoteType;
    NotificationsFragment fragment;

    public NotificationsAdapter(Context context, int layout, List<Notification> data) {
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

    public void setData(List<Notification> data) {
        this.data = data;
    }

    public void setSymbols(Set<String> symbols) {
        this.symbols = symbols;
    }

    public void setQuoteType(int quoteType) {
        this.quoteType = quoteType;
    }

    public void setFragment(NotificationsFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = lInflater.inflate(layout, parent, false);

        Notification quote = data.get(position);
//        LOGD(TAG, String.format("%d %s %s", position, quote.getQuoteSymbol(), quote.getQuoteName()));
        TextView text1 = view.findViewById(android.R.id.text1);
        TextView text2 = view.findViewById(android.R.id.text2);
        text1.setText(quote.getText());
//        DateTime dateTime = new DateTime(Long.valueOf(quote.getTimestamp()), DateTimeZone.forTimeZone(TimeZone.getDefault()));
        text2.setText(dtf.print(quote.getTimestamp()));

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
