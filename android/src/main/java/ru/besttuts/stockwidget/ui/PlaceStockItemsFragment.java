package ru.besttuts.stockwidget.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Map;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.io.HandleJSON;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;
import ru.besttuts.stockwidget.util.NotificationManager;
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ru.besttuts.stockwidget.ui.PlaceStockItemsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ru.besttuts.stockwidget.ui.PlaceStockItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceStockItemsFragment extends Fragment implements LoaderCallbacks<Cursor>,
        NotificationManager.ColorChangedListener, NotificationManager.OptionsItemSelectListener {

    private static final String TAG = makeLogTag(PlaceStockItemsFragment.class);

    // параметры для инициализации фрагмента, e.g. ARG_ITEM_NUMBER
    private static final String ARG_WIDGET_ID = "widgetId";

    private int mWidgetId;
    private int mWidgetItemsNumber;

    private OnFragmentInteractionListener mListener;

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
    public static PlaceStockItemsFragment newInstance(int widgetId) {
        PlaceStockItemsFragment fragment = new PlaceStockItemsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WIDGET_ID, widgetId);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceStockItemsFragment() {
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
        mMainView = inflater.inflate(R.layout.fragment_configure_stock_items, container, false);

        changeColor();

        // формируем столбцы сопоставления
        String[] from = new String[] { QuoteContract.ModelColumns.MODEL_NAME,
                QuoteContract.ModelColumns.MODEL_RATE,
                QuoteContract.ModelColumns.MODEL_CHANGE,
                QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE,
                QuoteContract.SettingColumns.SETTING_QUOTE_POSITION };

        int[] to = new int[] { R.id.tvName, R.id.tvRate, R.id.tvChange, R.id.tvChangePercentage,
                R.id.tvPosition };

        // создааем адаптер и настраиваем список
        mSimpleCursorAdapter = new MySimpleCursorAdapter(getActivity(), R.layout.configure_quote_grid_item, null, from, to, 0);
        GridView gridView = (GridView) mMainView.findViewById(R.id.gridView);
        gridView.setAdapter(mSimpleCursorAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StockItemTypeDialogFragment dialog = new StockItemTypeDialogFragment();
                dialog.set(position + 1, PlaceStockItemsFragment.this);
                dialog.show(getActivity().getSupportFragmentManager(), "StockItemTypeDialogFragment");
            }
        });

        // создаем лоадер для чтения данных
        Loader loader = getActivity().getSupportLoaderManager().getLoader(URL_LOADER);
        if(null == loader) {
            LOGD(TAG, "Loader is null");
            getActivity().getSupportLoaderManager().initLoader(URL_LOADER, null, this);
        } else {
            LOGD(TAG, "Loader is " + loader);
            loader.forceLoad();
        }

        Button button = (Button) mMainView.findViewById(R.id.btnAddQuote);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockItemTypeDialogFragment dialog = new StockItemTypeDialogFragment();
                dialog.set(mWidgetItemsNumber + 1, PlaceStockItemsFragment.this);
                dialog.show(getActivity().getSupportFragmentManager(), "StockItemTypeDialogFragment");
            }
        });

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
        mFetchQuote = null;
        Button button = (Button) mMainView.findViewById(R.id.btnAddQuote);
        if (0 < mWidgetItemsNumber) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
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
    public void changeColor() {
        // TODO: set color
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String color = sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE);
        mMainView.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void onOptionsItemSelectedInActivity(MenuItem item) {
        // Обработка нажатий на элемент ActionBar
        switch (item.getItemId()) {
            case R.id.action_add_quote:
                StockItemTypeDialogFragment dialog = new StockItemTypeDialogFragment();
                dialog.set(mWidgetItemsNumber + 1, PlaceStockItemsFragment.this);
                dialog.show(getActivity().getSupportFragmentManager(), "StockItemTypeDialogFragment");
                break;
            default:
                break;
        }
    }

    private void deleteItem(int pos) {
        Cursor cursor = (Cursor) mSimpleCursorAdapter.getItem(pos - 1);
        // извлекаем id записи и удаляем соответствующую запись в БД
        mDataSource.deleteSettingsByIdAndUpdatePositions(cursor.getString(cursor
                .getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_ID)), pos);
        // получаем новый курсор с данными
        getActivity().getSupportLoaderManager().getLoader(URL_LOADER).forceLoad();
    }

    private void onQuoteTypeSelected(int quoteTypePos, int position) {
        if (null == mListener) return;

        mListener.showQuotePickerActivity(quoteTypePos, position);

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

        LOGD(TAG, "onAttach");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (null != mDataSource) mDataSource.close(); // закрываем подключение при выходе
        getActivity().getSupportLoaderManager().destroyLoader(URL_LOADER);
        LOGD(TAG, "onDetach");
    }

    /**
     *
     * Этот интерфейс должен быть реализован Activity, которые содержат этот фрагмент,
     * чтобы этот фрагмент мог общаться с Activity и фрагментами содержащимися в ней.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name

        /**
         *
         * @param quoteTypeValue цифровое значение типа котировки
         * @param position Порядковый номер котировки на виджете.
         */
        public void showQuotePickerActivity(int quoteTypeValue, int position);
    }

    FetchQuote mFetchQuote;

    class MySimpleCursorAdapter extends SimpleCursorAdapter {
        MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            Cursor cursor = (Cursor) getItem(position);
            String symbol = cursor.getString(cursor.getColumnIndexOrThrow(
                    QuoteContract.ModelColumns.MODEL_ID));
            LOGD(TAG, "getView: symbol = " + symbol + " currentThread = " + Thread.currentThread());

            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.lLayoutRate);

            if (null == symbol || symbol.isEmpty()) {
                QuoteType quoteType = QuoteType.valueOf(cursor.getString(cursor
                        .getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_TYPE)));
                symbol = cursor.getString(cursor.getColumnIndexOrThrow(
                        QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL));
                ((TextView) view.findViewById(R.id.tvName)).setText(
                        Utils.getModelNameFromResourcesBySymbol(getActivity(), quoteType, symbol));


                layout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                if (null == mFetchQuote) {
                    LOGD(TAG, "before FetchQuote: getView: currentThread = " + Thread.currentThread());
                    LOGD(TAG, String.format("getView: quoteType = %s, symbol = %s", quoteType, symbol));

                    mFetchQuote = (FetchQuote) new FetchQuote(getActivity())
                            .execute(new String[]{String.valueOf(quoteType), symbol});
                }

                return view;
            }

            progressBar.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);

            Model model = QuoteDataSource.transformCursorToModel(cursor);
            ((TextView) view.findViewById(R.id.tvName)).setText(model.getName());
            ((TextView) view.findViewById(R.id.tvRate)).setText(model.getRateToString());

            TextView tvChange = (TextView) view.findViewById(R.id.tvChange);
            tvChange.setText(model.getChangeToString());

            TextView tvChangePercentage = (TextView) view.findViewById(R.id.tvChangePercentage);
            tvChangePercentage.setText(model.getPercentChange());

            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

            int color = getResources().getColor(R.color.arrow_green);
            if (0 < model.getChange()) {
                imageView.setImageResource(R.drawable.ic_widget_green_arrow_up);
            } else {
                imageView.setImageResource(R.drawable.ic_widget_green_arrow_down);
                color = getResources().getColor(R.color.arrow_red);
            }
            tvChange.setTextColor(color);
            tvChangePercentage.setTextColor(color);

            return view;
        }
    }

    public static class StockItemTypeDialogFragment extends DialogFragment {

        private static final String ARG_POSITION = "position";

        private int mPosition;
        private PlaceStockItemsFragment mFragment;

        public StockItemTypeDialogFragment() {}

        public void set(int position, PlaceStockItemsFragment fragment) {
            mPosition = position;
            mFragment = fragment;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(ARG_POSITION, mPosition);
            // сохраняем ссылку на фрагмент с котором будем взаимодействовать в дальнейшем
            setTargetFragment(mFragment, 0);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (null != savedInstanceState) {
                mPosition = savedInstanceState.getInt(ARG_POSITION);
            }
            if (null == mFragment) {
                /**
                 * Получаем ссылку на фрагмент PlaceStockItemsFragment.
                 * Она сохраняется при поворотах экрана!
                 */
                mFragment = (PlaceStockItemsFragment) getTargetFragment();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.select_quotes_type)
                    .setItems(R.array.quotes_type_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // The 'which' argument contains the index position
                            // of the selected item
                            mFragment.onQuoteTypeSelected(which, mPosition);

                            // закрываем диалоговое окно
                            StockItemTypeDialogFragment.this.dismiss();
                        }
                    });

            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mFragment.deleteItem(mPosition);
                }
            }).setNegativeButton(R.string.cancel, null);

            return builder.create();
        }
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
            Cursor cursor = mDataSource.getCursorSettingsWithModelByWidgetId(mWidgetId);

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

    private class FetchQuote extends AsyncTask<String, Void, Model> {

        private final Context mContext;

        private FetchQuote(Context context) {
            mContext = context;
        }

        @Override
        protected Model doInBackground(String... params) { //TODO: Написать Unit-тесты!!!

            RemoteYahooFinanceDataFetcher dataFetcher = new RemoteYahooFinanceDataFetcher();
            QuoteDataSource dataSource = new QuoteDataSource(mContext);
            dataSource.open();

            QuoteType quoteType = QuoteType.valueOf(params[0]);
            String symbol = params[1];

            LOGD(TAG, String.format("FetchQuote.doInBackground: quoteType = %s, symbol = %s", quoteType, symbol));

            dataFetcher.populateQuoteSet(quoteType, symbol);

            HandleJSON handleJSON = new HandleJSON();
            try {
                handleJSON.readAndParseJSON(dataFetcher.downloadQuotes());

                Map<String, Model> symbolModelMap = handleJSON.getSymbolModelMap();

                for (Model model: symbolModelMap.values()) {
                    dataSource.addModelRec(model);
                    LOGD(TAG, "FetchQuote.doInBackground: currentThread = " + Thread.currentThread());

                    return model;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                dataSource.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Model model) {
            if (null == model) return;

            LOGD(TAG, "FetchQuote.onPostExecute: currentThread = " + Thread.currentThread());
            FragmentActivity activity = getActivity();
            if (null == activity) return;

            LoaderManager loaderManager = activity.getSupportLoaderManager();
            if (null == loaderManager) return;

            Loader loader = loaderManager.getLoader(URL_LOADER);
            if (null != loader) {
                LOGD(TAG, "Loader is " + loader);
                loader.forceLoad();
            }

        }
    }

}
