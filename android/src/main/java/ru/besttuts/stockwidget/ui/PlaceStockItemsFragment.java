package ru.besttuts.stockwidget.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.util.NotificationManager;

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
        NotificationManager.ColorChangedListener {

    private static final String TAG = makeLogTag(PlaceStockItemsFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_WIDGET_ID = "widgetId";
    private static final String ARG_WIDGET_ITEMS_NUMBER = "widgetItemsNumber";

    // TODO: Rename and change types of parameters
    private int mWidgetId;
    private int mWidgetItemsNumber;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private QuoteDataSource mDataSource;

    private GridView gridView;
    private SimpleCursorAdapter scAdapter;

    private View view;

    // Identifies a particular Loader being used in this component
    private static final int URL_LOADER = 0;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param widgetId Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigureMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaceStockItemsFragment newInstance(int widgetId, String param2) {
        PlaceStockItemsFragment fragment = new PlaceStockItemsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WIDGET_ID, widgetId);
        args.putInt(ARG_WIDGET_ITEMS_NUMBER, 0);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceStockItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWidgetId = getArguments().getInt(ARG_WIDGET_ID);
            mWidgetItemsNumber = getArguments().getInt(ARG_WIDGET_ITEMS_NUMBER);
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

        view = inflater.inflate(R.layout.fragment_configure_stock_items, container, false);

        changeColor();

        // формируем столбцы сопоставления
        String[] from = new String[] { QuoteContract.SettingColumns.SETTING_QUOTE_SYMBOL,
                QuoteContract.SettingColumns.SETTING_QUOTE_POSITION };
        int[] to = new int[] { R.id.tvName, R.id.tvRate };

        // создааем адаптер и настраиваем список
        scAdapter = new MySimpleCursorAdapter(getActivity(), R.layout.configure_quote_grid_item, null, from, to, 0);
        gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(scAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                if (null != mListener) mListener.setWidgetItemPosition(position);
                DialogFragment dialog = new StockItemTypeDialogFragment(position);
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

        Button button = (Button) view.findViewById(R.id.btnAddQuote);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) mListener.setWidgetItemPosition(mWidgetItemsNumber);
                DialogFragment dialog = new StockItemTypeDialogFragment(mWidgetItemsNumber + 1);
                dialog.show(getActivity().getSupportFragmentManager(), "StockItemTypeDialogFragment");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LOGD(TAG, "onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGD(TAG, "onResume");
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
        LOGD(TAG, "onDestroy");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(getActivity(), mDataSource, getArguments().getInt(ARG_WIDGET_ID));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        scAdapter.changeCursor(data);
//        gridView.setAdapter(scAdapter);
//        scAdapter.notifyDataSetChanged();
//        gridView.postInvalidate();
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                gridView.setVisibility(View.VISIBLE);
//                gridView.invalidate();
//            }
//        });
        mWidgetItemsNumber = data.getCount();
        getArguments().putInt(ARG_WIDGET_ITEMS_NUMBER, data.getCount());
        LOGD(TAG, "swapCursor: cursor.getCount = " + getArguments().getInt(ARG_WIDGET_ITEMS_NUMBER));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Clears out the adapter's reference to the Cursor.
         * This prevents memory leaks.
         */
        scAdapter.changeCursor(null);
    }

    @Override
    public void changeColor() {
        // TODO: set color
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String color = sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR, "#34495e");
        view.setBackgroundColor(Color.parseColor(color));
    }

    private class OnClickListenerImpl implements View.OnClickListener {
        private int widgetItemPosition;

        private OnClickListenerImpl(int widgetItemPosition) {
            this.widgetItemPosition = widgetItemPosition;
        }

        @Override
        public void onClick(View v) {
            if (null != mListener) mListener.setWidgetItemPosition(widgetItemPosition);
            DialogFragment dialog = new StockItemTypeDialogFragment(widgetItemPosition + 1);
            dialog.show(getActivity().getSupportFragmentManager(), "StockItemTypeDialogFragment");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    private void onQuoteTypeSelected(int quoteTypePos) {
        if (null == mListener) return;
        mListener.onConfigureMenuFragmentInteraction(quoteTypePos);


//        switch (quoteTypePos) {
//            case 0:
//                mListener.onConfigureMenuFragmentInteraction(QuoteType.CURRENCY_EXCHANGE);
//                break;
//            case 1:
//                mListener.onConfigureMenuFragmentInteraction(QuoteType.GOODS);
//                break;
//        }
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onConfigureMenuFragmentInteraction(int quoteTypeValue);

        public void setWidgetItemPosition(int widgetItemPosition);
    }

    class MySimpleCursorAdapter extends SimpleCursorAdapter {
        MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }

    class StockItemTypeDialogFragment extends DialogFragment {

        private int mPosition;

        StockItemTypeDialogFragment(int position) {
            mPosition = position;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.select_quotes_type)
                    .setItems(R.array.quotes_type_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            onQuoteTypeSelected(which);
                        }
                    });

            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Cursor cursor = (Cursor) scAdapter.getItem(mPosition);
                    // извлекаем id записи и удаляем соответствующую запись в БД
                    mDataSource.deleteSettingsById(cursor.getString(cursor
                            .getColumnIndexOrThrow(QuoteContract.SettingColumns.SETTING_ID)));
                    // получаем новый курсор с данными
                    getActivity().getSupportLoaderManager().getLoader(URL_LOADER).forceLoad();
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
            Cursor cursor = mDataSource.getCursorSettingsByWidgetId(mWidgetId);

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
