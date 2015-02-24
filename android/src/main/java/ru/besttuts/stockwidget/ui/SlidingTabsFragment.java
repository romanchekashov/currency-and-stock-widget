package ru.besttuts.stockwidget.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.ui.view.SlidingTabLayout;
import ru.besttuts.stockwidget.util.NotificationManager;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 24.01.2015.
 */
public class SlidingTabsFragment extends Fragment
        implements NotificationManager.ColorChangedListener {

    private static final String TAG = makeLogTag(SlidingTabsFragment.class);

    /**
     * A custom {@link android.support.v4.view.ViewPager} CtryNm strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link android.support.v4.view.ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    private SlidingTabsPagerAdapter mSlidingTabsPagerAdapter;

    private static final String ARG_WIDGET_ID = "widgetId";
    private int mWidgetId;

    private OnFragmentInteractionListener mListener;

    public static SlidingTabsFragment newInstance(int widgetId) {
        SlidingTabsFragment fragment = new SlidingTabsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WIDGET_ID, widgetId);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LOGD(TAG, "savedInstanceState = "+ savedInstanceState);
        LOGD(TAG, "getArguments() = "+ getArguments());

        mWidgetId = getArguments().getInt(ARG_WIDGET_ID);
        LOGD(TAG, "mWidgetId = "+ mWidgetId);

        NotificationManager.addListener(this);

//        if (null != savedInstanceState) {
//            mWidgetId = savedInstanceState.getInt(ARG_WIDGET_ID);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager.removeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mSlidingTabsPagerAdapter = new SlidingTabsPagerAdapter(
                getActivity().getSupportFragmentManager(), mWidgetId);
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSlidingTabsPagerAdapter);
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (0 == position) {
                    if (null != mListener) mListener.showAddQuoteItem(true);
                } else {
                    if (null != mListener) mListener.showAddQuoteItem(false);
                }
            }
        });
        changeColor();
        // END_INCLUDE (setup_slidingtablayout)
    }

    @Override
    public void changeColor() {
        if (null == mSlidingTabLayout) return;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String color = sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor(color);
            }

            @Override
            public int getDividerColor(int position) {
                return Color.parseColor(color);
            }
        });
    }

    public interface OnFragmentInteractionListener {

        public void showAddQuoteItem(boolean isVisible);

    }

    class SlidingTabsPagerAdapter extends FragmentPagerAdapter {

        private int mWidgetId;

        SlidingTabsPagerAdapter(FragmentManager fm, int widgetId) {
            super(fm);
            mWidgetId = widgetId;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = TrackingQuotesFragment.newInstance(mWidgetId);
                    break;
                case 1:
                    fragment = new ConfigPreferenceFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = String.valueOf(super.getPageTitle(position));
            switch (position) {
                case 0:
                    title = getString(R.string.action_quotes);
                    break;
                case 1:
                    title = getString(R.string.action_settings);
                    break;
            }
            return title;
        }
    }
}
