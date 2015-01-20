package ru.besttuts.stockwidget.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import ru.besttuts.stockwidget.R;

public class CurrencyExchangeFragment extends Fragment implements IQuoteTypeFragment {

    private static final String LOG_TAG = "EconomicWidget.CurrencyExchangeFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FROM = "from";
    private static final String ARG_TO = "to";

    // TODO: Rename and change types of parameters
    private String mCurrencyFrom;
    private String mCurrencyTo;

    private String mCurrencyExchangeFromTo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param widgetItemPosition Parameter 1.
     * @param quoteTypeValue     Parameter 2.
     * @return A new instance of fragment CurrencyExchangeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrencyExchangeFragment newInstance(int widgetItemPosition, int quoteTypeValue) {
        CurrencyExchangeFragment fragment = new CurrencyExchangeFragment();
        Bundle args = new Bundle();
        args.putInt("widgetItemPosition", widgetItemPosition);
        args.putInt("quoteTypeValue", quoteTypeValue);
        fragment.setArguments(args);
        return fragment;
    }

    public CurrencyExchangeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrencyFrom = getArguments().getString(ARG_FROM);
            mCurrencyTo = getArguments().getString(ARG_TO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currency_exchange, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerCurrencyFrom);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.currency_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) adapter.getItem(position);
                getArguments().putString(ARG_FROM, str.substring(str.length() - 4, str.length() - 1));
                Log.d(LOG_TAG, "onItemSelected: "+ getArguments().getString(ARG_FROM));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinner1 = (Spinner) view.findViewById(R.id.spinnerCurrencyTo);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),
                R.array.currency_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) adapter1.getItem(position);
                getArguments().putString(ARG_TO, str.substring(str.length() - 4, str.length() - 1));
                Log.d(LOG_TAG, "onItemSelected: "+ getArguments().getString(ARG_TO));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        return getArguments().getString(ARG_FROM) + getArguments().getString(ARG_TO);
    }

}
