package ru.besttuts.stockwidget.ui.fragments.quoteprovider;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.model.QuoteProvider;
import ru.besttuts.stockwidget.ui.fragments.ConfigPreferenceFragment;

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
public class QuoteProviderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<QuoteProvider>> {

    private static final String TAG = makeLogTag(QuoteProviderFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GOOD_ITEM = "good";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // цвет для выделенного элемента списка
    private int mColor;

    private OnFragmentInteractionListener mListener;

    private QuoteProviderAdapter quoteProviderAdapter;


    // Идентификатор загрузчика используемый в данном компоненте
    private static final int URL_LOADER = 0;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    public static QuoteProviderFragment newInstance(int widgetItemPosition) {
        QuoteProviderFragment fragment = new QuoteProviderFragment();
        Bundle args = new Bundle();
        args.putInt("widgetItemPosition", widgetItemPosition);
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
    public QuoteProviderFragment() {
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

        View view = inflater.inflate(R.layout.fragment_quote_provider, container, false);

        // формируем столбцы сопоставления
        String[] from = new String[]{QuoteContract.QuoteColumns.QUOTE_NAME,
                QuoteContract.QuoteColumns.QUOTE_SYMBOL};

        int[] to = new int[]{android.R.id.text1, android.R.id.text2};

        // создааем адаптер и настраиваем список
        quoteProviderAdapter = new QuoteProviderAdapter(getActivity(), android.R.layout.simple_list_item_2, new ArrayList<>());
//        quoteProviderAdapter.setProviderCodes(mSymbols);
//        quoteProviderAdapter.setQuoteType(mQuoteType);
        quoteProviderAdapter.setFragment(this);
        mListView = view.findViewById(R.id.fragment_quote_provider_listView);
//        listView.setBackground(getResources().getDrawable(R.drawable.bg_key));
        mListView.setAdapter(quoteProviderAdapter);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {

            TextView text2 = view1.findViewById(android.R.id.text2);
            String providerCode = String.valueOf(text2.getText());
//            if (mSymbols.contains(providerCode)) {
//                view1.setBackgroundColor(Color.TRANSPARENT);
//                mSymbols.remove(providerCode);
//            } else {
//                mSymbols.add(providerCode);
//                setSelectedBgView(view1);
//            }
//            if (0 < mSymbols.size()) {
//                if (null != mListener) mListener.showAcceptItem(true);
//            } else {
//                if (null != mListener) mListener.showAcceptItem(false);
//            }
            LOGD(TAG, "onItemClick: " + quoteProviderAdapter.getItem(position));
        });

        reload();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (0 < quoteProviderAdapter.getCount()) {
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
    public Loader<List<QuoteProvider>> onCreateLoader(int id, Bundle args) {
        return new QuoteProviderLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<QuoteProvider>> loader, List<QuoteProvider> data) {
        quoteProviderAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<QuoteProvider>> loader) {
        quoteProviderAdapter.setData(new ArrayList<>());
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
        void onFragmentInteraction(String id);

        void showAcceptItem(boolean isVisible);
    }


    public void setSelectedBgView(View view) {
        if (0 == mColor) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String bgColor = "#" + ConfigPreferenceFragment.KEY_PREF_BG_VISIBILITY_DEFAULT_VALUE +
                    sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                            ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE).substring(1);
            mColor = Color.parseColor(bgColor);
        }

        view.setBackgroundColor(mColor);
    }
}
