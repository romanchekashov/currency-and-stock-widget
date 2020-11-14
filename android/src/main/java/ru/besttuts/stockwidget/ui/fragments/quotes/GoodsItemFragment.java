package ru.besttuts.stockwidget.ui.fragments.quotes;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.model.Quote;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class GoodsItemFragment extends AbsQuoteSelectionFragment
        implements LoaderManager.LoaderCallbacks<List<Quote>> {

    private static final String TAG = makeLogTag(GoodsItemFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GOOD_ITEM = "good";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private QuotesAdapter quotesAdapter;


    // Идентификатор загрузчика используемый в данном компоненте
    private static final int URL_LOADER = 0;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    public static GoodsItemFragment newInstance(int widgetItemPosition, int quoteType) {
        GoodsItemFragment fragment = new GoodsItemFragment();
        Bundle args = new Bundle();
        args.putInt("widgetItemPosition", widgetItemPosition);
        args.putInt(ARG_QUOTE_TYPE, quoteType);
        fragment.setArguments(args);
        return fragment;
    }

    public int getWidgetItemPosition() {
        return getArguments().getInt("widgetItemPosition", 0);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GoodsItemFragment() {
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

        View view = inflater.inflate(R.layout.fragment_my_quotes, container, false);

        // формируем столбцы сопоставления
        String[] from = new String[]{QuoteContract.QuoteColumns.QUOTE_NAME,
                QuoteContract.QuoteColumns.QUOTE_SYMBOL};

        int[] to = new int[]{android.R.id.text1, android.R.id.text2};

        // создааем адаптер и настраиваем список
        quotesAdapter = new QuotesAdapter(getActivity(), android.R.layout.simple_list_item_2, new ArrayList<Quote>());
        quotesAdapter.setSymbols(mSymbols);
        quotesAdapter.setQuoteType(mQuoteType);
        quotesAdapter.setFragment(this);
        mListView = (ListView) view.findViewById(R.id.listView2);
//        listView.setBackground(getResources().getDrawable(R.drawable.bg_key));
        mListView.setAdapter(quotesAdapter);
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
                    if (null != mListener) mListener.showAcceptItem(true);
                } else {
                    if (null != mListener) mListener.showAcceptItem(false);
                }
                LOGD(TAG, "onItemClick: " + quotesAdapter.getItem(position));
            }
        });

        reload();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (0 < mSymbols.size()) {
            if (null != mListener) mListener.showAcceptItem(true);
        } else {
            if (null != mListener) mListener.showAcceptItem(false);
        }
        LOGD(TAG, "onResume: currentThread = " + Thread.currentThread());
        reload();
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

    private void reload() {
        // создаем лоадер для чтения данных
        Loader loader = getActivity().getSupportLoaderManager().getLoader(URL_LOADER);
        if (null == loader) {
            LOGD(TAG, "Loader is null");
            getActivity().getSupportLoaderManager().initLoader(URL_LOADER, null, this);
        } else {
            LOGD(TAG, "Loader is " + loader);
            loader.forceLoad();
        }
    }

    @Override
    public Loader<List<Quote>> onCreateLoader(int id, Bundle args) {
        return new QuoteLoader(getActivity(), mQuoteType);
    }

    @Override
    public void onLoadFinished(Loader<List<Quote>> loader, List<Quote> data) {
        quotesAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Quote>> loader) {
        quotesAdapter.setData(new ArrayList<Quote>());
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
        public void onFragmentInteraction(String id);

        public void showAcceptItem(boolean isVisible);

    }

}
