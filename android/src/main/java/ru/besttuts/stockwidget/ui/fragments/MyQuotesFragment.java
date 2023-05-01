package ru.besttuts.stockwidget.ui.fragments;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.loaders.MyCursorLoader;
import ru.besttuts.stockwidget.ui.activities.QuotePickerActivity;

/**
 *
 */
public class MyQuotesFragment extends AbsQuoteSelectionFragment implements LoaderCallbacks<Cursor> {

    private static final String TAG = makeLogTag(MyQuotesFragment.class);

    // параметры для инициализации фрагмента, e.g. ARG_ITEM_NUMBER
    private static final String ARG_WIDGET_ID = "widgetId";
    private static final String ARG_SYMBOLS = "symbols";

    private int mWidgetId;
    private int mWidgetItemsNumber;
    private String mSymbol;
    private Set<String> mSymbols;

    private SimpleCursorAdapter mSimpleCursorAdapter;

    private View mMainView;

    // Идентификатор загрузчика используемый в данном компоненте
    private static final int URL_LOADER = 0;

    private OnFragmentInteractionListener mListener;

    /**
     * Используйте этот фабричный метод для создания
     * нового объекта этого фрагмента с предоставляемыми параметрами.
     *
     * @param widgetId идентификатор виджета.
     * @return Новый объект фрагмента PlaceStockItemsFragment.
     */
    public static MyQuotesFragment newInstance(int widgetId, int quoteType) {
        MyQuotesFragment fragment = new MyQuotesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WIDGET_ID, widgetId);
        args.putInt(ARG_QUOTE_TYPE, quoteType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getWidgetItemPosition() {
        return 0;
    }

    @Override
    public String[] getSelectedSymbols() {
        if (null == mSymbols) return new String[0];

        String[] symbols = new String[mSymbols.size()];
        return mSymbols.toArray(symbols);
    }

    public void deleteSelectedSymbols() {
        String[] symbols = getSelectedSymbols();
        QuotePickerActivity.mDataSource.deleteQuotesByIds(symbols);
        mSymbols = new HashSet<>();
        Toast.makeText(getActivity(), String.format("%d quotes deleted!", symbols.length),
                Toast.LENGTH_SHORT).show();
        Loader loader = getActivity().getSupportLoaderManager().getLoader(URL_LOADER);
        if (null == loader) {
            LOGD(TAG, "Loader is null");
            getActivity().getSupportLoaderManager().initLoader(URL_LOADER, null, this);
        } else {
            LOGD(TAG, "Loader is " + loader);
            loader.forceLoad();
        }
    }

    // Необходим пустой общедоступный конструктор
    public MyQuotesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSymbols = new HashSet<>();
        if (getArguments() != null) {
            mWidgetId = getArguments().getInt(ARG_WIDGET_ID);
        }
        if (null != savedInstanceState) {
            mSymbols.addAll(savedInstanceState.getStringArrayList(ARG_SYMBOLS));
        }

        LOGD(TAG, String.format("onCreate: mWidgetId = %d, mWidgetItemsNumber = %d",
                mWidgetId, mWidgetItemsNumber));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_SYMBOLS, mSymbol);
        ArrayList<String> list = new ArrayList<String>(mSymbols.size());
        list.addAll(mSymbols);
        outState.putStringArrayList(ARG_SYMBOLS, list);
    }

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LOGD(TAG, "onCreateView");

        // Вызываем и заполняем отображение для этого фрагмента
        mMainView = inflater.inflate(R.layout.fragment_my_quotes, container, false);

        // формируем столбцы сопоставления
        String[] from = new String[]{QuoteContract.QuoteColumns.QUOTE_NAME,
                QuoteContract.QuoteColumns.QUOTE_SYMBOL};

        int[] to = new int[]{android.R.id.text1, android.R.id.text2};

        // создааем адаптер и настраиваем список
        mSimpleCursorAdapter = new MySimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2, null, from, to, 0);

        mListView = (ListView) mMainView.findViewById(R.id.listView2);
//        listView.setBackground(getResources().getDrawable(R.drawable.bg_key));
        mListView.setAdapter(mSimpleCursorAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String symbol = String.valueOf(((TextView) view.findViewById(android.R.id.text2)).getText());
                if (mSymbols.contains(symbol)) {
                    view.setBackgroundColor(Color.TRANSPARENT);
                    mSymbols.remove(symbol);
                } else {
                    mSymbols.add(symbol);
                    setSelectedBgView(view);
                }
                if (0 < mSymbols.size()) {
                    if (null != mListener) mListener.showDeleteItem(true);
                } else {
                    if (null != mListener) mListener.showDeleteItem(false);
                }
                LOGD(TAG, "onItemClick: " + position + ", symbol = " + symbol + ", view: " + view);
            }
        });

        // создаем лоадер для чтения данных
        Loader loader = getActivity().getSupportLoaderManager().getLoader(URL_LOADER);
        if (null == loader) {
            LOGD(TAG, "Loader is null");
            getActivity().getSupportLoaderManager().initLoader(URL_LOADER, null, this);
        } else {
            LOGD(TAG, "Loader is " + loader);
            loader.forceLoad();
        }

        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (0 < mSymbols.size()) {
            if (null != mListener) mListener.showDeleteItem(true);
        } else {
            if (null != mListener) mListener.showDeleteItem(false);
        }
        LOGD(TAG, "onResume: currentThread = " + Thread.currentThread());
        Loader loader = getActivity().getSupportLoaderManager().getLoader(URL_LOADER);
        if (null != loader) {
            LOGD(TAG, "Loader is " + loader);
            loader.forceLoad();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOGD(TAG, "onDestroy");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        LOGD(TAG, "onCreateLoader: currentThread = " + Thread.currentThread());
        return new MyCursorLoader(getActivity(), QuotePickerActivity.mDataSource, mQuoteType);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSimpleCursorAdapter.changeCursor(data);
        mWidgetItemsNumber = data.getCount();
        LOGD(TAG, "onLoadFinished: currentThread = " + Thread.currentThread());
        LOGD(TAG, "swapCursor: cursor.getCount = mWidgetItemsNumber = " + mWidgetItemsNumber);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Удаляем ссылку на Cursor в адаптере.
         * Это предотвращает утечку памяти.
         */
        mSimpleCursorAdapter.changeCursor(null);
    }

    private void deleteItem(int pos) {
        Cursor cursor = (Cursor) mSimpleCursorAdapter.getItem(pos - 1);
        // извлекаем id записи и удаляем соответствующую запись в БД
        QuotePickerActivity.mDataSource.deleteSettingsByIdAndUpdatePositions(cursor.getString(
                cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_ID)), pos);
        // получаем новый курсор с данными
        getActivity().getSupportLoaderManager().getLoader(URL_LOADER).forceLoad();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().getSupportLoaderManager().destroyLoader(URL_LOADER);
        mListener = null;
        LOGD(TAG, "onDetach");
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void showDeleteItem(boolean isVisible);

        public void deleteQuote(String[] symbols);
    }

}
