package ru.besttuts.stockwidget.ui.fragments.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.provider.model.Notification;
import ru.besttuts.stockwidget.provider.model.Quote;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;


public class NotificationsFragment extends Fragment {

    private static final String TAG = makeLogTag(NotificationsFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GOOD_ITEM = "good";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_WIDGET_ID = "widgetId";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private NotificationsAdapter quotesAdapter;

    private final CompositeDisposable mDisposable = new CompositeDisposable();


    // Идентификатор загрузчика используемый в данном компоненте
    private static final int URL_LOADER = 0;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    public static NotificationsFragment newInstance(int widgetId) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WIDGET_ID, widgetId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotificationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        LOGD(TAG, "onCreate");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LOGD(TAG, "onCreateView:");

        View view = inflater.inflate(R.layout.fragment_my_quotes, container, false);

        // создааем адаптер и настраиваем список
        quotesAdapter = new NotificationsAdapter(getActivity(), android.R.layout.simple_list_item_2, Collections.emptyList());
        quotesAdapter.setFragment(this);
        mListView = view.findViewById(R.id.listView2);
//        listView.setBackground(getResources().getDrawable(R.drawable.bg_key));
        mListView.setAdapter(quotesAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification selected = (Notification) quotesAdapter.getItem(position);
//                String symbol = String.valueOf(((TextView) view.findViewById(android.R.id.text2)).getText());
//
//                if (mSymbols.contains(symbol)) {
//                    view.setBackgroundColor(Color.TRANSPARENT);
//                    mSymbols.remove(symbol);
//                    DbProvider.getInstance().removeTempQuotes(selected);
//                } else {
//                    DbProvider.getInstance().addTempQuotes(selected);
//                    mSymbols.add(symbol);
//                    setSelectedBgView(view);
//                }
//
//                if (0 < mSymbols.size()) {
//                    if (null != mListener) mListener.showAcceptItem(true);
//                } else {
//                    if (null != mListener) mListener.showAcceptItem(false);
//                }

                LOGD(TAG, "onItemClick: " + selected);
            }
        });

        mDisposable.add(DbProvider.notificationDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notifications -> {
                    quotesAdapter.setData(notifications);
                    quotesAdapter.notifyDataSetChanged();
                    LOGD(TAG, "notifications: " + notifications.size());
                }, throwable -> LOGE(TAG, "Unable to get quotes", throwable)));


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGD(TAG, "onResume: currentThread = " + Thread.currentThread());
    }

}
