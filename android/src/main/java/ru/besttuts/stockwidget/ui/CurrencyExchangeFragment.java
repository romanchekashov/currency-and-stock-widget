package ru.besttuts.stockwidget.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ru.besttuts.stockwidget.R;

public class CurrencyExchangeFragment extends Fragment implements IQuoteTypeFragment {

    private static final String LOG_TAG = "EconomicWidget.CurrencyExchangeFragment";

    // параметры для инициализации фрагмента, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FROM = "from";
    private static final String ARG_FROM_POSITION = "from_position";
    private static final String ARG_TO = "to";
    private static final String ARG_TO_POSITION = "to_position";

    private String mCurrencyFrom;
    private int mCurrencyFromPosition;
    private String mCurrencyTo;
    private int mCurrencyToPosition;

    /**
     * Используйте этот фабричный метод для создания
     * нового объекта этого фрагмента с предоставляемыми параметрами.
     *
     * @param widgetItemPosition Parameter 1.
     * @param quoteTypeValue     Parameter 2.
     * @return Новый объект фрагмента CurrencyExchangeFragment.
     */
    public static CurrencyExchangeFragment newInstance(int widgetItemPosition, int quoteTypeValue) {
        CurrencyExchangeFragment fragment = new CurrencyExchangeFragment();
        Bundle args = new Bundle();
        args.putInt("widgetItemPosition", widgetItemPosition);
        args.putInt("quoteTypeValue", quoteTypeValue);
        fragment.setArguments(args);
        return fragment;
    }

    public CurrencyExchangeFragment() {
        // Необходим пустой общедоступный конструктор
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrencyFrom = getArguments().getString(ARG_FROM);
            mCurrencyFromPosition = getArguments().getInt(ARG_FROM_POSITION);
            mCurrencyTo = getArguments().getString(ARG_TO);
            mCurrencyToPosition = getArguments().getInt(ARG_TO_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Вызываем и заполняем отображение для этого фрагмента
        View view = inflater.inflate(R.layout.fragment_currency_exchange, container, false);

        final TextView tvCurrency = (TextView) view.findViewById(R.id.tvCurrency);

        final Spinner spinner = (Spinner) view.findViewById(R.id.spinnerCurrencyFrom);
        // Создаем ArrayAdapter с параметрами: массив строк, layout по умолчанию для выпадающего списка
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.currency_array, android.R.layout.simple_spinner_item);
        // Указываем layout используемый для отображения списка вариантов
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Предоставляем адаптер для выпадающего списка
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrencyFromPosition = position;
                String str = (String) adapter.getItem(position);
                mCurrencyFrom = str.substring(str.length() - 4, str.length() - 1);
                tvCurrency.setText(mCurrencyFrom +"/"+ mCurrencyTo);

                Log.d(LOG_TAG, "onItemSelected: " + mCurrencyFrom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(mCurrencyFromPosition, false);
                spinner.setSelection(mCurrencyFromPosition);
                ((ArrayAdapter) spinner.getAdapter()).notifyDataSetChanged();
            }
        });

        final Spinner spinner1 = (Spinner) view.findViewById(R.id.spinnerCurrencyTo);
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),
                R.array.currency_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrencyToPosition = position;
                String str = (String) adapter1.getItem(position);
                mCurrencyTo = str.substring(str.length() - 4, str.length() - 1);
                tvCurrency.setText(mCurrencyFrom +"/"+ mCurrencyTo);

                Log.d(LOG_TAG, "onItemSelected: "+ mCurrencyTo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mCurrencyFrom) {
            Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinnerCurrencyFrom);
            spinner.setSelection(mCurrencyFromPosition);
        }
        if (null != mCurrencyTo) {
            Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinnerCurrencyTo);
            spinner.setSelection(mCurrencyToPosition);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null == getArguments()) {
            setArguments(new Bundle());
        }
        getArguments().putString(ARG_FROM, mCurrencyFrom);
        getArguments().putInt(ARG_FROM_POSITION, mCurrencyFromPosition);
        getArguments().putString(ARG_TO, mCurrencyTo);
        getArguments().putInt(ARG_TO_POSITION, mCurrencyToPosition);
    }

    @Override
    public int getWidgetItemPosition() {
        return getArguments().getInt("widgetItemPosition", 0);
    }

    @Override
    public int getQuoteTypeValue() {
        return getArguments().getInt("quoteTypeValue", 0);
    }

    @Override
    public String getSymbol() {
        return mCurrencyFrom + mCurrencyTo;
    }

}
