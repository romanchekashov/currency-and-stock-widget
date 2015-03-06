package ru.besttuts.stockwidget.ui;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 07.01.2015.
 */
public class QuotePickerActivity extends ActionBarActivity
        implements GoodsItemFragment.OnFragmentInteractionListener,
        MyQuotesFragment.OnFragmentInteractionListener {

    private static final String TAG = makeLogTag(QuotePickerActivity.class);
    static QuoteDataSource mDataSource;
    private int mAppWidgetId;
    private int mQuoteTypeValue;
    private int mWidgetItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second_configure);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

//        Utils.onActivityCreateSetActionBarColor(getSupportActionBar());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(EconomicWidgetConfigureActivity.getColor(this, false));
        }
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(EconomicWidgetConfigureActivity.getColor(this, true)));



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (null == savedInstanceState) {
            Bundle b = getIntent().getExtras();
            mAppWidgetId = b.getInt(EconomicWidgetConfigureActivity.ARG_WIDGET_ID);
            mQuoteTypeValue = b.getInt(EconomicWidgetConfigureActivity.ARG_QUOTE_TYPE_VALUE);
            mWidgetItemPosition = b.getInt(EconomicWidgetConfigureActivity.ARG_WIDGET_ITEM_POSITION);

            LOGD(TAG, "widgetId = " + mAppWidgetId);
            LOGD(TAG, "quoteTypeValue = " + mQuoteTypeValue);
            LOGD(TAG, "widgetItemPosition = " + mWidgetItemPosition);

            Fragment fragment = null;
            switch (mQuoteTypeValue) {
                case QuoteType.CURRENCY:
                    fragment = CurrencyExchangeFragment.newInstance(mWidgetItemPosition, mQuoteTypeValue);
                    break;
                case QuoteType.GOODS:
                case QuoteType.INDICES:
                case QuoteType.STOCK:
                    fragment = GoodsItemFragment.newInstance(mWidgetItemPosition, mQuoteTypeValue);
                    break;
                case QuoteType.QUOTES:
                    fragment = MyQuotesFragment.newInstance(mAppWidgetId, mQuoteTypeValue);
                    break;
            }

            if (null == fragment) {
                finish();
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.second_cont, fragment).commit();
        } else {
            mAppWidgetId = savedInstanceState.getInt(
                    EconomicWidgetConfigureActivity.ARG_WIDGET_ID);
            mQuoteTypeValue = savedInstanceState.getInt(
                    EconomicWidgetConfigureActivity.ARG_QUOTE_TYPE_VALUE);
            mWidgetItemPosition = savedInstanceState.getInt(
                    EconomicWidgetConfigureActivity.ARG_WIDGET_ITEM_POSITION);
        }

        switch (mQuoteTypeValue) {
            case QuoteType.CURRENCY:
                getSupportActionBar().setTitle(R.string.configure_menu_item_currency);
                break;
            case QuoteType.GOODS:
                getSupportActionBar().setTitle(R.string.configure_menu_item_goods);
                break;
            case QuoteType.INDICES:
                getSupportActionBar().setTitle(R.string.configure_menu_item_indices);
                break;
            case QuoteType.STOCK:
                getSupportActionBar().setTitle(R.string.configure_menu_item_stock);
                break;
            case QuoteType.QUOTES:
                getSupportActionBar().setTitle(R.string.configure_menu_my_quotes);
                break;
        }

        // создаем объект для создания и управления версиями БД
        mDataSource = new QuoteDataSource(this);

        LOGD(TAG, "onCreate");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EconomicWidgetConfigureActivity.ARG_WIDGET_ID, mAppWidgetId);
        outState.putInt(EconomicWidgetConfigureActivity.ARG_QUOTE_TYPE_VALUE, mQuoteTypeValue);
        outState.putInt(EconomicWidgetConfigureActivity.ARG_WIDGET_ITEM_POSITION, mWidgetItemPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.second_activity_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private Menu mMenu;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.action_show_search);
        if (QuoteType.QUOTES != mQuoteTypeValue) {
            register.setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.second_cont);
        if (fragment1 instanceof IQuoteTypeFragment) {
            String[] symbols = ((IQuoteTypeFragment) fragment1).getSelectedSymbols();
            if (0 == symbols.length) {
                menu.findItem(R.id.action_accept).setVisible(false);
                if (fragment1 instanceof MyQuotesFragment) {
                    menu.findItem(R.id.action_delete).setVisible(false);
                }
            }
        }

        mMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка нажатий на элемент ActionBar
        switch (item.getItemId()) {
            case R.id.action_show_search:
                Intent intent = new Intent(this, SearchableQuoteActivity.class);
                Bundle b = new Bundle();
                b.putInt(EconomicWidgetConfigureActivity.ARG_WIDGET_ID, mAppWidgetId);
                b.putInt(EconomicWidgetConfigureActivity.ARG_QUOTE_TYPE_VALUE, mQuoteTypeValue);
                intent.putExtras(b);
                startActivity(intent);
                return true;
            case R.id.action_accept:
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.second_cont);
                LOGD(TAG, "onOptionsItemSelected: fragment: " + fragment.getClass().getName());
                if (fragment instanceof IQuoteTypeFragment) {
                    IQuoteTypeFragment quoteTypeFragment = (IQuoteTypeFragment) fragment;
                    mDataSource.addSettingsRec(mAppWidgetId, mWidgetItemPosition,
                            quoteTypeFragment.getQuoteType(), quoteTypeFragment.getSelectedSymbols());
                }
                finish();
                return true;
            case R.id.action_delete:
                Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.second_cont);

                if (fragment1 instanceof MyQuotesFragment) {
                    ((MyQuotesFragment) fragment1).deleteSelectedSymbols();
                }
                return true;
            case android.R.id.home: // Respond to the action bar's Up/Home button
//                Intent upIntent = new Intent(this, EconomicWidgetConfigureActivity.class);
//                upIntent.setAction("android.appwidget.action.APPWIDGET_CONFIGURE");
//                upIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
//                NavUtils.navigateUpTo(this, upIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mDataSource) mDataSource.close(); // закрываем соединение с БД
        LOGD(TAG, "onDestroy");
    }

    @Override
    public void onFragmentInteraction(String id) {
        LOGD(TAG, "id: " + id);
    }

    @Override
    public void showDeleteItem(boolean isVisible) {
        if(null != mMenu) {
            mMenu.findItem(R.id.action_delete).setVisible(isVisible);
            mMenu.findItem(R.id.action_accept).setVisible(isVisible);
        }
    }

    @Override
    public void showAcceptItem(boolean isVisible) {
        if(null != mMenu) {
            mMenu.findItem(R.id.action_accept).setVisible(isVisible);
        }
    }

    @Override
    public void deleteQuote(String[] symbols) {
        mDataSource.deleteQuotesByIds(symbols);
    }

}
