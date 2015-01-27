package ru.besttuts.stockwidget.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 07.01.2015.
 */
public class SecondConfigureActivity extends ActionBarActivity
        implements GoodsItemFragment.OnFragmentInteractionListener {

    private static final String TAG = makeLogTag(SecondConfigureActivity.class);
    private QuoteDataSource mDataSource;
    private int mAppWidgetId;
    private int mQuoteTypeValue;
    private int mWidgetItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second_configure);

        Bundle b = getIntent().getExtras();
        mAppWidgetId = b.getInt("widgetId");
        mQuoteTypeValue = b.getInt("quoteTypeValue");
        mWidgetItemPosition = b.getInt("widgetItemPosition");

        LOGD(TAG, "widgetId = " + mAppWidgetId);
        LOGD(TAG, "quoteTypeValue = " + mQuoteTypeValue);
        LOGD(TAG, "widgetItemPosition = " + mWidgetItemPosition);

        Fragment fragment = null;
        switch (mQuoteTypeValue) {
            case 0:
                fragment = CurrencyExchangeFragment.newInstance(mWidgetItemPosition, mQuoteTypeValue);
                break;
            case 1:
                fragment = GoodsItemFragment.newInstance(mWidgetItemPosition, mQuoteTypeValue);
                break;
        }

        if (null == fragment) {
            finish();
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.second_cont, fragment).commit();

        // создаем объект для создания и управления версиями БД
        mDataSource = new QuoteDataSource(this);
        mDataSource.open();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.second_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка нажатий на элемент ActionBar
        switch (item.getItemId()) {
            case R.id.action_accept:
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.second_cont);
                LOGD(TAG, "onOptionsItemSelected: fragment: " + fragment.getClass().getName());

                if (fragment instanceof CurrencyExchangeFragment) {
                    mDataSource.addSettingsRec(mAppWidgetId, mWidgetItemPosition,
                            QuoteType.CURRENCY_EXCHANGE.toString(),
                            ((CurrencyExchangeFragment) fragment).getSymbol());
                } else if (fragment instanceof GoodsItemFragment) {
                    mDataSource.addSettingsRec(mAppWidgetId, mWidgetItemPosition,
                            QuoteType.GOODS.toString(),
                            ((GoodsItemFragment) fragment).getSymbol());
                }
                finish();
                return true;
            case R.id.menuQuotes:
                Toast.makeText(getApplicationContext(), "menuQuotes", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuDisplay:
                Toast.makeText(getApplicationContext(), "menuDisplay", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuSettings:
                Toast.makeText(getApplicationContext(), "menuSettings", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_place, new ConfigPreferenceFragment()).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mDataSource) mDataSource.close(); // закрываем соединение с БД
    }

    @Override
    public void onFragmentInteraction(String id) {
        LOGD(TAG, "id: " + id);
    }

}
