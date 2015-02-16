package ru.besttuts.stockwidget.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ActionProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.service.UpdateService;
import ru.besttuts.stockwidget.util.NotificationManager;
import ru.besttuts.stockwidget.util.Utils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;


/**
 * The configuration screen for the {@link EconomicWidget EconomicWidget} AppWidget.
 */
public class EconomicWidgetConfigureActivity extends ActionBarActivity
        implements SlidingTabsFragment.OnFragmentInteractionListener,
        PlaceStockItemsFragment.OnFragmentInteractionListener,
        NotificationManager.ColorChangedListener {

    private static final String TAG = makeLogTag(EconomicWidgetConfigureActivity.class);

    public static final String ARG_WIDGET_ID = "widgetId";
    public static final String ARG_QUOTE_TYPE_VALUE = "quoteTypeValue";
    public static final String ARG_WIDGET_ITEM_POSITION = "widgetItemPosition";

    private static final String PREFS_NAME = "ru.besttuts.stockwidget.ui.EconomicWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    static QuoteDataSource mDataSource;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    // Текущий, выбранный тип котировки
    int position = 0;

    public EconomicWidgetConfigureActivity() {
        super();
    }

    public int getWidgetId() {
        return mAppWidgetId;
    }

    @Override
    public void showQuotePickerActivity(int quoteTypeValue, int position) {

        Intent intent = new Intent(this, QuotePickerActivity.class);
        Bundle b = new Bundle();
        b.putInt(ARG_WIDGET_ID, mAppWidgetId);
        b.putInt(ARG_QUOTE_TYPE_VALUE, quoteTypeValue);
        b.putInt(ARG_WIDGET_ITEM_POSITION, position);
        intent.putExtras(b);
        startActivity(intent);

    }

    private Menu mMenu;

    @Override
    public void showAddQuoteItem(boolean isVisible) {
        if(null != mMenu) {
            mMenu.findItem(R.id.action_add_quote).setVisible(isVisible);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        mMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка нажатий на элемент ActionBar
        switch (item.getItemId()) {
            case R.id.action_accept:
                acceptBtnPressed();
                return true;
            case R.id.action_add_quote:
                NotificationManager.notifyOptionsItemSelected(item);
                return true;
            case R.id.action_github:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/romanchekashov/currency-and-stock-widget"));
                startActivity(browserIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void acceptBtnPressed() {
        final Context context = EconomicWidgetConfigureActivity.this;

        Intent intent = new Intent(context.getApplicationContext(), UpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{mAppWidgetId});
        // Update the widgets via the service
        context.startService(intent);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        PreferenceManager.setDefaultValues(this, R.xml.preference_config, false);

        setContentView(R.layout.activity_economic_widget_configure);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NotificationManager.addListener(this);

        changeColor();

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
//        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = SlidingTabsFragment.newInstance(mAppWidgetId);
            fragmentTransaction.replace(R.id.fragment_place, fragment);
//        fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            position = savedInstanceState.getInt("position");
        }

        // создаем объект для создания и управления версиями БД
        mDataSource = new QuoteDataSource(this);
        mDataSource.open();

        LOGD(TAG, "onCreate");

    }

    @Override
    protected void onResume() {
        super.onResume();
        LOGD(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mDataSource) mDataSource.close();
        NotificationManager.removeListener(this);
        LOGD(TAG, "onDestroy");
    }

    static void saveLastUpdateTimePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_lastupdatetime", text);
        prefs.commit();
    }

    static String loadLastUpdateTimePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_lastupdatetime", null);
        if (titleValue != null) {
            return titleValue;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yy");

            return dateFormat.format(Calendar.getInstance().getTime());
        }
    }

    static void deleteLastUpdateTimePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_lastupdatetime");
        prefs.commit();
    }

    @Override
    public void changeColor() {
        Utils.onActivityCreateSetActionBarColor(getSupportActionBar());
    }

}



