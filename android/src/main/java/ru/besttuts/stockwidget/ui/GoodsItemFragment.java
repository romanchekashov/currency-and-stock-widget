package ru.besttuts.stockwidget.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.ui.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class GoodsItemFragment extends Fragment implements IQuoteTypeFragment,
        AbsListView.OnItemClickListener {

    private static final String LOG_TAG = "EconomicWidget.GoodsItemFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GOOD_ITEM = "good";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static GoodsItemFragment newInstance(int widgetItemPosition, int quoteTypeValue) {
        GoodsItemFragment fragment = new GoodsItemFragment();
        Bundle args = new Bundle();
        args.putInt("widgetItemPosition", widgetItemPosition);
        args.putInt("quoteTypeValue", quoteTypeValue);
        fragment.setArguments(args);
        return fragment;
    }

    public int getWidgetItemPosition() {
        return getArguments().getInt("widgetItemPosition", 0);
    }

    public int getQuoteTypeValue() {
        return getArguments().getInt("quoteTypeValue", 0);
    }

    @Override
    public String getSymbol() {
        return getArguments().getString(ARG_GOOD_ITEM);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GoodsItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

//        DummyContent.addItem(new DummyContent.DummyItem("1", String.format("%s(GCF15.CMX)",getString(R.string.goods_gold))));
//        DummyContent.addItem(new DummyContent.DummyItem("2", String.format("%s(SIF15.CMX)",getString(R.string.goods_silver))));
//        DummyContent.addItem(new DummyContent.DummyItem("3", String.format("%s(PLF15.NYM)",getString(R.string.goods_platinum))));
//        DummyContent.addItem(new DummyContent.DummyItem("4", String.format("%s(PAF15.NYM)",getString(R.string.goods_palladium))));
//        DummyContent.addItem(new DummyContent.DummyItem("5", String.format("%s(HGF15.CMX)", getString(R.string.goods_copper))));
//        // TODO: Change Adapter to display your content
//        mAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.array.good_array,
//                R.layout.goods_list_item, R.id.tvGood);
        mAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.good_array, R.layout.goods_list_item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
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
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setSelected(true);
        if (null != mListener) {
//            String str = DummyContent.ITEMS.get(position).content;
            String str = (String) mAdapter.getItem(position);
            getArguments().putString(ARG_GOOD_ITEM, str.substring(str.lastIndexOf("(") + 1, str.length() - 1));

            Log.d(LOG_TAG, "onItemClick: " + getArguments().getString(ARG_GOOD_ITEM));

            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction();
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
    }

}
