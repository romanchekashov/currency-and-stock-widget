package ru.besttuts.stockwidget.ui.fragments.tracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.db.DbNotificationManager;
import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Setting;
import ru.besttuts.stockwidget.provider.model.wrap.ModelSetting;
import ru.besttuts.stockwidget.sync.MyFinanceWS;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;
import ru.besttuts.stockwidget.sync.sparklab.dto.QuoteDto;
import ru.besttuts.stockwidget.ui.activities.DynamicWebViewActivity;
import ru.besttuts.stockwidget.ui.activities.EconomicWidgetConfigureActivity;
import ru.besttuts.stockwidget.util.CustomConverter;
import ru.besttuts.stockwidget.util.NotificationManager;
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Фрагмет с отслеживаемыми котировками.
 */
public class TrackingQuotesFragment extends Fragment
        implements NotificationManager.ColorChangedListener, NotificationManager.OptionsItemSelectListener,
        AdapterView.OnItemClickListener {

    private static final String TAG = makeLogTag(TrackingQuotesFragment.class);

    // параметры для инициализации фрагмента, e.g. ARG_ITEM_NUMBER
    private static final String ARG_WIDGET_ID = "widgetId";
    public static final String ARG_URL = "url";

    private int mWidgetId;
    public static int mWidgetItemsNumber;


    private DbProvider mDbProvider;
    private DbNotificationManager mNotifier;
    private DbNotificationManager.Listener mDbListener = new DbNotificationManager.Listener() {
        @Override
        public void onDataUpdated() {
            updateSettings();
        }
    };
    private DbProvider.ResultCallback<List<ModelSetting>> mUpdateCallback = null;

    private OnFragmentInteractionListener mListener;

//    private SimpleCursorAdapter mSimpleCursorAdapter;
    private TrackingQuotesAdapter trackingQuotesAdapter;

    private View mMainView;
    private GridView gridView;

    private volatile boolean isQuotesFetched = false;
    /**
     * Используйте этот фабричный метод для создания
     * нового объекта этого фрагмента с предоставляемыми параметрами.
     *
     * @param widgetId идентификатор виджета.
     * @return Новый объект фрагмента PlaceStockItemsFragment.
     */
    public static TrackingQuotesFragment newInstance(int widgetId) {
        TrackingQuotesFragment fragment = new TrackingQuotesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WIDGET_ID, widgetId);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackingQuotesFragment() {
        // Необходим пустой общедоступный конструктор
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWidgetId = getArguments().getInt(ARG_WIDGET_ID);
        }

        NotificationManager.addListener(this);

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
        String[] from = new String[]{
                QuoteContract.ModelColumns.MODEL_NAME,
                QuoteContract.ModelColumns.MODEL_RATE,
                QuoteContract.ModelColumns.MODEL_CURRENCY,
                QuoteContract.ModelColumns.MODEL_CHANGE,
                QuoteContract.ModelColumns.MODEL_PERCENT_CHANGE,
                QuoteContract.SettingColumns.SETTING_QUOTE_POSITION};

        int[] to = new int[]{R.id.tvName, R.id.tvRate, R.id.tvCurrency, R.id.tvChange,
                R.id.tvChangePercentage, R.id.tvPosition};

        // создааем адаптер и настраиваем список
//        mSimpleCursorAdapter = new MySimpleCursorAdapter(getActivity(), R.layout.configure_quote_grid_item, null, from, to, 0);
        trackingQuotesAdapter = new TrackingQuotesAdapter(getActivity(), R.layout.configure_quote_grid_item, new ArrayList<ModelSetting>());
        gridView = (GridView) mMainView.findViewById(R.id.gridView);
        gridView.setAdapter(trackingQuotesAdapter);
        gridView.setOnItemClickListener(this);

        TextView tvNYSEInfo = (TextView) mMainView.findViewById(R.id.tvNYSEInfo);
        tvNYSEInfo.setText(Utils.getNYSEInfo(getContext()));

        Button button = (Button) mMainView.findViewById(R.id.btnAddQuote);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuoteTypeDialog();
            }
        });

        mDbProvider = DbProvider.getInstance();
        mNotifier = DbNotificationManager.getInstance();
        mNotifier.addListener(mDbListener);
        updateSettings();

        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGD(TAG, "onResume: currentThread = " + Thread.currentThread());
        isQuotesFetched = false;
        updateSettings();
    }

    private void onSettingsUpdated(List<ModelSetting> result){
//        if(null == result || 0 >= result.getCount()){
//            // Удаляем ссылку на Cursor в адаптере. Это предотвращает утечку памяти.
//            mSimpleCursorAdapter.changeCursor(null);
//            return;
//        }

//        mSimpleCursorAdapter.changeCursor(result);
        trackingQuotesAdapter.setData(result);
        mWidgetItemsNumber = result.size();
        mFetchQuote = null;
        Button button = (Button) mMainView.findViewById(R.id.btnAddQuote);
        if (0 < mWidgetItemsNumber) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }

        if(!isQuotesFetched){
            new FetchQuote(getActivity()).execute(new String[]{
                    String.valueOf(mWidgetId)
            });
        }

        LOGD(TAG, "swapCursor: cursor.getCount = mWidgetItemsNumber = " + mWidgetItemsNumber);
    }

    private void updateSettings() {
        cancelUpdateSettings();
        mUpdateCallback = new DbProvider.ResultCallback<List<ModelSetting>>() {
            @Override
            public void onFinished(List<ModelSetting> result) {
                if (mUpdateCallback != this) return;
                onSettingsUpdated(result);
            }
        };
        mDbProvider.getSettingsWithModelByWidgetId(mWidgetId, mUpdateCallback);
    }

    private void cancelUpdateSettings() {
        if (mUpdateCallback == null) return;
        mUpdateCallback = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LOGD(TAG, "onDestroyView");

        NotificationManager.removeListener(this);
        cancelUpdateSettings();
        mNotifier.removeListener(mDbListener);
    }

    @Override
    public void changeColor() {

        // TODO: set color
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String color = sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
//                ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE);
        mMainView.setBackgroundColor(EconomicWidgetConfigureActivity.getColor(getActivity(), true));
    }

    public void showQuoteTypeDialog() {
        StockItemTypeDialogFragment dialog = new StockItemTypeDialogFragment();
        dialog.set(-1, this);
        dialog.show(getActivity().getSupportFragmentManager(), "StockItemTypeDialogFragment");
    }

    @Override
    public void onOptionsItemSelectedInActivity(MenuItem item) {
        // Обработка нажатий на элемент ActionBar
        switch (item.getItemId()) {
            case R.id.action_add_quote:
                LOGD(TAG, "onOptionsItemSelectedInActivity: " + item);
                showQuoteTypeDialog();
                break;
            default:
                break;
        }
    }

    private void deleteItem(int pos) {
        ModelSetting modelSetting = (ModelSetting) trackingQuotesAdapter.getItem(pos - 1);
        LOGD(TAG, String.format("deleteItem: pos = %d, settingId = %s, _id = %d",
                pos, modelSetting.getSetting().getId(), modelSetting.getSetting().get_id()));
        // извлекаем id записи и удаляем соответствующую запись в БД
        mDbProvider.deleteSettingsByIdAndUpdatePositions(
                modelSetting.getSetting().getId(), pos);

//        updateSettings();
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
        LOGD(TAG, "onDetach");
    }

    private int mLastSelectedItemPosition = -1;

    /**
     * Отрабатываем клик по элементу сетки(Grid)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        LOGD(TAG, "(onItemClick): " + String.format("view = %s, pos = %d, id = %d", view, position, id));

        clearGridViewCellSelection();

        mLastSelectedItemPosition = position;
        view.setBackgroundColor(Color.parseColor("#33ffffff"));

        PopupWindow popupWindow = popupWindowDogs(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if(0 == (position + 1) % gridView.getNumColumns()) {
                popupWindow.showAsDropDown(view, -popupWindow.getWidth(), -view.getHeight());
                return;
            }
        }
        // show the list view as dropdown
        popupWindow.showAsDropDown(view, view.getWidth(), -view.getHeight());

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                LOGD(TAG, "(onDismiss): " + String.format("mLastSelectedItemPosition = %d", mLastSelectedItemPosition));
                clearGridViewCellSelection();
                mLastSelectedItemPosition = -1;
            }
        });

    }

    private void clearGridViewCellSelection(){
        if(null == gridView) return;
        LOGD(TAG, "(clearGridViewCellSelection):");
        View view;
        for (int i = 0, count = gridView.getChildCount(); i < count; i++){
            view = gridView.getChildAt(i);
            if (null != view) view.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
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
         * @param quoteTypeValue цифровое значение типа котировки
         * @param position       Порядковый номер котировки на виджете.
         */
        public void showQuotePickerActivity(int quoteTypeValue, int position);

    }

    FetchQuote mFetchQuote;

    public static class StockItemTypeDialogFragment extends DialogFragment {

        private static final String ARG_POSITION = "position";

        private int mPosition = -1;
        private TrackingQuotesFragment mFragment;

        public StockItemTypeDialogFragment() {
        }

        public void set(int position, TrackingQuotesFragment fragment) {
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
                mFragment = (TrackingQuotesFragment) getTargetFragment();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.select_quotes_type)
                    .setItems(R.array.quotes_type_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // The 'which' argument contains the index position
                            // of the selected item
                            if (-1 == mPosition) {
                                mFragment.onQuoteTypeSelected(which, mWidgetItemsNumber + 1);
                            } else {
                                mFragment.onQuoteTypeSelected(which, mPosition);
                            }

                            // закрываем диалоговое окно
                            StockItemTypeDialogFragment.this.dismiss();
                        }
                    });

//            if (-1 != mPosition) {
//                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mFragment.deleteItem(mPosition);
//                    }
//                });
//            }

            builder.setNegativeButton(R.string.cancel, null);

            return builder.create();
        }
    }

    private class FetchQuote extends AsyncTask<String, Void, List<Model>> {

        private final String TAG = makeLogTag(FetchQuote.class);

        private final Context mContext;

        private FetchQuote(Context context) {
            mContext = context;
        }

        @Override
        protected List<Model> doInBackground(String... params) { //TODO: Написать Unit-тесты!!!

            int widgetId = Integer.parseInt(params[0]);

            List<Model> models = mDbProvider.getModelsByWidgetId(widgetId);

            RemoteYahooFinanceDataFetcher dataFetcher = new RemoteYahooFinanceDataFetcher();

            List<Setting> settingsWithoutModel = mDbProvider.getCursorSettingsWithoutModelByWidgetId(widgetId);

            LOGD(TAG, String.format("[doInBackground]: cursor.getCount() = %d", settingsWithoutModel.size()));

            if (settingsWithoutModel.isEmpty()) {
                return models;
            }

            List<String> symbols = new ArrayList<>(settingsWithoutModel.size());

            for (Setting setting: settingsWithoutModel) {
                symbols.add(setting.getQuoteSymbol());
            }

            try {
                List<QuoteDto> quoteDtos = new MyFinanceWS(mContext).getQuotes(symbols);
                for (QuoteDto dto: quoteDtos) {
                    Model model = CustomConverter.toModel(dto);
                    mDbProvider.addModelRec(model);
                    models.add(model);
                }

                LOGD(TAG, "FetchQuote.doInBackground: currentThread = " + Thread.currentThread());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return models;
        }

        @Override
        protected void onPostExecute(List<Model> models) {

            if (0 == models.size()) return;

            FragmentActivity activity = getActivity();
            if (null == activity) return;

            LOGD(TAG, "FetchQuote.onPostExecute: currentThread = " + Thread.currentThread());
            updateSettings();

            isQuotesFetched = true;
        }
    }

    public PopupWindow popupWindowDogs(final int position) {

        // initialize a pop up window type
        final PopupWindow popupWindow = new PopupWindow(getActivity());

        // the drop down list is a list view
        ListView listViewDogs = (ListView) getActivity().getLayoutInflater().inflate(R.layout.popup_window, null, false);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, new String[]{getString(R.string.dynamic),
                getString(R.string.change), getString(R.string.remove)});

        // set our adapter and pass our pop up window contents
        listViewDogs.setAdapter(adapter);

        // set the item click listener
        listViewDogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                LOGD(TAG, adapter.getItem(pos));

                switch (pos) {
                    case 0:
//                        Cursor cursor = (Cursor) mSimpleCursorAdapter.getItem(position);
                        String url = "http://finance.yahoo.com/q";
//                        String symbol = cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.ModelColumns.MODEL_ID));
//                        if(QuoteType.CURRENCY == cursor.getInt(cursor.getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_QUOTE_TYPE))) {
//                            url += String.format("?s=%s=X&ql=1", symbol);
//                        } else {
//                            url += String.format("?s=%s&ql=1", symbol);
//                        }

                        Intent intent = new Intent(getActivity(), DynamicWebViewActivity.class);
                        intent.putExtra(ARG_URL, url);

                        popupWindow.dismiss();
                        clearGridViewCellSelection();

                        startActivity(intent);
                        break;
                    case 1:
                        StockItemTypeDialogFragment dialog = new StockItemTypeDialogFragment();
                        dialog.set(position + 1, TrackingQuotesFragment.this);
                        popupWindow.dismiss();
                        clearGridViewCellSelection();
                        dialog.show(getActivity().getSupportFragmentManager(), "StockItemTypeDialogFragment");
                        break;
                    case 2:
                        popupWindow.dismiss();
                        clearGridViewCellSelection();
                        deleteItem(position + 1);
                        break;
                }
            }
        });

        // some other visual settings
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setFocusable(true);
//        popupWindow.setWidth(100);
//        popupWindow.setWidth(330);
//        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int popupWidth;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point windowSize = new Point();
            display.getSize(windowSize);
            popupWidth = windowSize.x / 2;
        } else {
            popupWidth = display.getWidth() / 2;
        }

        if(350 < popupWidth) {
            popupWindow.setWidth(350);
        } else {
            popupWindow.setWidth(popupWidth);
        }

        // set the list view as pop up window content
        popupWindow.setContentView(listViewDogs);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                LOGD(TAG, "(popupWindowDogs)(onDismiss)");
                clearGridViewCellSelection();
            }
        });

        popupWindow.setOutsideTouchable(true);
        return popupWindow;
    }

}