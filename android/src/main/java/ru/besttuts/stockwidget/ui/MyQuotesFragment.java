package ru.besttuts.stockwidget.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.util.NotificationManager;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 *
 */
public class MyQuotesFragment extends Fragment implements LoaderCallbacks<Cursor>,
        NotificationManager.OptionsItemSelectListener {

    private static final String TAG = makeLogTag(MyQuotesFragment.class);

    // параметры для инициализации фрагмента, e.g. ARG_ITEM_NUMBER
    private static final String ARG_WIDGET_ID = "widgetId";

    private int mWidgetId;
    private int mWidgetItemsNumber;

    private QuoteDataSource mDataSource;

    private SimpleCursorAdapter mSimpleCursorAdapter;

    private View mMainView;

    // Идентификатор загрузчика используемый в данном компоненте
    private static final int URL_LOADER = 0;

    /**
     * Используйте этот фабричный метод для создания
     * нового объекта этого фрагмента с предоставляемыми параметрами.
     *
     * @param widgetId идентификатор виджета.
     * @return Новый объект фрагмента PlaceStockItemsFragment.
     */
    public static MyQuotesFragment newInstance(int widgetId) {
        MyQuotesFragment fragment = new MyQuotesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WIDGET_ID, widgetId);
        fragment.setArguments(args);
        return fragment;
    }

    public MyQuotesFragment() {
        // Необходим пустой общедоступный конструктор
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWidgetId = getArguments().getInt(ARG_WIDGET_ID);
        }

        NotificationManager.addListener(this);

        mDataSource = new QuoteDataSource(getActivity());
        mDataSource.open();

        LOGD(TAG, String.format("onCreate: mWidgetId = %d, mWidgetItemsNumber = %d",
                mWidgetId, mWidgetItemsNumber));
    }

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
        mSimpleCursorAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2, null, from, to, 0);

        ListView listView = (ListView) mMainView.findViewById(R.id.listView2);
//        listView.setBackground(getResources().getDrawable(R.drawable.bg_key));
        listView.setAdapter(mSimpleCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                view.setBackgroundColor(Color.GREEN);
                LOGD(TAG, "onItemClick: " + mSimpleCursorAdapter.getItem(position));
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
        NotificationManager.removeListener(this);
        if (null != mDataSource) mDataSource.close();
        LOGD(TAG, "onDestroy");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(getActivity(), mDataSource, getArguments().getInt(ARG_WIDGET_ID));
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

    @Override
    public void onOptionsItemSelectedInActivity(MenuItem item) {
        // Обработка нажатий на элемент ActionBar
//        switch (item.getItemId()) {
//            case R.id.action_add_quote:
//                StockItemTypeDialogFragment dialog = new StockItemTypeDialogFragment();
//                dialog.set(mWidgetItemsNumber + 1, MyQuotesFragment.this);
//                dialog.show(getActivity().getSupportFragmentManager(), "StockItemTypeDialogFragment");
//                break;
//            default:
//                break;
//        }
    }

    private void deleteItem(int pos) {
        Cursor cursor = (Cursor) mSimpleCursorAdapter.getItem(pos - 1);
        // извлекаем id записи и удаляем соответствующую запись в БД
        mDataSource.deleteSettingsByIdAndUpdatePositions(cursor.getString(cursor
                .getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_ID)), pos);
        // получаем новый курсор с данными
        getActivity().getSupportLoaderManager().getLoader(URL_LOADER).forceLoad();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (null != mDataSource) mDataSource.close(); // закрываем подключение при выходе
        getActivity().getSupportLoaderManager().destroyLoader(URL_LOADER);
        LOGD(TAG, "onDetach");
    }

    static class MyCursorLoader extends CursorLoader {

        QuoteDataSource mDataSource;
        int mWidgetId;

        public MyCursorLoader(Context context, QuoteDataSource dataSource, int widgetId) {
            super(context);
            mDataSource = dataSource;
            mWidgetId = widgetId;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = mDataSource.getCursorMyQuotes();

            LOGD(TAG, "loadInBackground: currentThread = " + Thread.currentThread());

            LOGD(TAG, "loadInBackground: cursor.getCount: " + cursor.getCount());
//            try {
//                TimeUnit.SECONDS.sleep(3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return cursor;
        }

    }

}
