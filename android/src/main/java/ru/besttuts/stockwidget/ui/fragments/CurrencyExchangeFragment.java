package ru.besttuts.stockwidget.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ru.besttuts.stockwidget.R;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class CurrencyExchangeFragment extends Fragment implements IQuoteTypeFragment {

    private static final String TAG = makeLogTag(CurrencyExchangeFragment.class);

    // параметры для инициализации фрагмента, e.g. ARG_ITEM_NUMBER
    private static final String ARG_QUOTE_TYPE = "quoteType";
    private static final String ARG_FROM = "from";
    private static final String ARG_FROM_POSITION = "from_position";
    private static final String ARG_TO = "to";
    private static final String ARG_TO_POSITION = "to_position";

    private static final int CURRENCY_POSITION_USD = 149;
    private static final int CURRENCY_POSITION_RUB = 119;
    private static final int CURRENCY_POSITION_UAH = 60;

    private int mQuoteType;
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
        args.putInt(ARG_QUOTE_TYPE, quoteTypeValue);
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
            mQuoteType = getArguments().getInt(ARG_QUOTE_TYPE);
            mCurrencyFrom = getArguments().getString(ARG_FROM);
            mCurrencyFromPosition = getArguments().getInt(ARG_FROM_POSITION);
            mCurrencyTo = getArguments().getString(ARG_TO);
            mCurrencyToPosition = getArguments().getInt(ARG_TO_POSITION);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null == getArguments()) {
            setArguments(new Bundle());
        }
        getArguments().putInt(ARG_QUOTE_TYPE, mQuoteType);
        getArguments().putString(ARG_FROM, mCurrencyFrom);
        getArguments().putInt(ARG_FROM_POSITION, mCurrencyFromPosition);
        getArguments().putString(ARG_TO, mCurrencyTo);
        getArguments().putInt(ARG_TO_POSITION, mCurrencyToPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Вызываем и заполняем отображение для этого фрагмента
        View view = inflater.inflate(R.layout.fragment_currency_exchange, container, false);

        final TextView tvCurrency = view.findViewById(R.id.tvCurrency);

        final Spinner spinner = view.findViewById(R.id.spinnerCurrencyFrom);
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

                LOGD(TAG, "onItemSelected: " + mCurrencyFrom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(mCurrencyFromPosition, false);
                spinner.setSelection(mCurrencyFromPosition);
                ((ArrayAdapter) spinner.getAdapter()).notifyDataSetChanged();
            }
        });

        final Spinner spinner1 = view.findViewById(R.id.spinnerCurrencyTo);
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

                LOGD(TAG, "onItemSelected: "+ mCurrencyTo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String lng = getResources().getConfiguration().locale.getLanguage();
        LOGD(TAG, "locale.getLanguage: " + lng);

        if ("ru".equals(lng)) {
            spinner1.setSelection(CURRENCY_POSITION_RUB, false);
        } else if ("uk".equals(lng)) {
            spinner1.setSelection(CURRENCY_POSITION_UAH, false);
        } else {
            spinner1.setSelection(CURRENCY_POSITION_USD, false);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mCurrencyFrom) {
            Spinner spinner = getActivity().findViewById(R.id.spinnerCurrencyFrom);
            spinner.setSelection(mCurrencyFromPosition);
        }
        if (null != mCurrencyTo) {
            Spinner spinner = getActivity().findViewById(R.id.spinnerCurrencyTo);
            spinner.setSelection(mCurrencyToPosition);
        }
    }

    @Override
    public int getWidgetItemPosition() {
        return getArguments().getInt("widgetItemPosition", 0);
    }

    @Override
    public int getQuoteType() {
        return mQuoteType;
    }

    @Override
    public String[] getSelectedSymbols() {
        return new String[]{mCurrencyFrom + mCurrencyTo};
    }

}
