package ru.besttuts.stockwidget.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import ru.besttuts.stockwidget.R;

/**
 * Created by roman on 07.01.2015.
 */
public class SecondConfigureActivity extends ActionBarActivity
        implements GoodsItemFragment.OnFragmentInteractionListener {

    final String LOG_TAG = "EconomicWidget.SecondConfigureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.second_configure);
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE
                && isLarge()) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            GoodsItemFragment details = GoodsItemFragment
                    .newInstance(getIntent().getIntExtra("position", 0), getIntent().getIntExtra("position", 0));
            getSupportFragmentManager().beginTransaction().add(R.id.second_cont, details).commit();
        }
    }

    boolean isLarge() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.i(LOG_TAG, "id: " + id);
    }

}
