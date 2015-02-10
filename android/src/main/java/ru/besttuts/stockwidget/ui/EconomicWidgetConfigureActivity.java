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

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.service.UpdateService;
import ru.besttuts.stockwidget.util.NotificationManager;
import ru.besttuts.stockwidget.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;


/**
 * The configuration screen for the {@link EconomicWidget EconomicWidget} AppWidget.
 */
public class EconomicWidgetConfigureActivity extends ActionBarActivity
        implements GoodsItemFragment.OnFragmentInteractionListener,
        PlaceStockItemsFragment.OnFragmentInteractionListener,
        NotificationManager.ColorChangedListener {

    private static final String TAG = makeLogTag(EconomicWidgetConfigureActivity.class);

    public static final String ARG_WIDGET_ID = "widgetId";
    public static final String ARG_QUOTE_TYPE_VALUE = "quoteTypeValue";
    public static final String ARG_WIDGET_ITEM_POSITION = "widgetItemPosition";

    private static final String PREFS_NAME = "ru.besttuts.stockwidget.ui.EconomicWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    private static final int MY_QUOTES_VALUE = 4;

    private QuoteDataSource mDataSource;

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

//        if (MY_QUOTES_VALUE == quoteTypeValue) {
//            Intent intent = new Intent(this, SearchableQuoteActivity.class);
//            Bundle b = new Bundle();
//            b.putInt("widgetId", mAppWidgetId);
//            intent.putExtras(b);
//            startActivity(intent);
//            return;
//        }

        Intent intent = new Intent(this, QuotePickerActivity.class);
        Bundle b = new Bundle();
        b.putInt(ARG_WIDGET_ID, mAppWidgetId);
        b.putInt(ARG_QUOTE_TYPE_VALUE, quoteTypeValue);
        b.putInt(ARG_WIDGET_ITEM_POSITION, position);
        intent.putExtras(b);
        startActivity(intent);

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void showAcceptItem(boolean isVisible) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
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
//            case R.id.menuQuotes:
//                Toast.makeText(getApplicationContext(), "menuQuotes", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.menuDisplay:
//                Toast.makeText(getApplicationContext(), "menuDisplay", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.menuSettings:
//                Toast.makeText(getApplicationContext(), "menuSettings", Toast.LENGTH_SHORT).show();
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_place, new ConfigPreferenceFragment()).commit();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void acceptBtnPressed(){
        final Context context = EconomicWidgetConfigureActivity.this;

        // When the button is clicked, store the string locally
        saveTitlePref(context, mAppWidgetId, "EXAMPLE");

        // It is the responsibility of the configuration activity to update the app widget
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        EconomicWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId, new ArrayList<Model>());

        Intent intent = new Intent(context.getApplicationContext(),UpdateService.class);
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
//        mAppWidgetText.setText(loadTitlePref(EconomicWidgetConfigureActivity.this, mAppWidgetId));

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
    }

    void showQuoteFragment(QuoteType quoteType) {
//        if (mIsDualPane) {
//            GoodsItemFragment details = (GoodsItemFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.cont);
//            if (details == null || details.getQuoteTypeValue() != quoteTypeValue) {
//                details = GoodsItemFragment.newInstance(widgetItemPosition, quoteTypeValue);
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.cont, details).commit();
//            }
//        } else {
//            startActivity(new Intent(this, QuotePickerActivity.class)
//                    .putExtra("quoteTypeValue", quoteType.getValue())
//                    .putExtra("widgetItemPosition", widgetItemPosition));
//        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = EconomicWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
//            String widgetText = mAppWidgetText.getText().toString();
            saveTitlePref(context, mAppWidgetId, "EXAMPLE");

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            EconomicWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId, new ArrayList<Model>(), true);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
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

//    public static void saveConnectionStatusPref(Context context, int appWidgetId, String text) {
//        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
//        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_connectionstatus", text);
//        prefs.commit();
//    }
//    public static String loadConnectionStatusPref(Context context, int appWidgetId) {
//        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
//        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_connectionstatus", null);
//        return titleValue;
//    }
//    public static void deleteConnectionStatusPref(Context context, int appWidgetId) {
//        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
//        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_connectionstatus");
//        prefs.commit();
//    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }

    @Override
    public void changeColor() {
        Utils.onActivityCreateSetActionBarColor(getSupportActionBar());
    }

    public static class ConfigActionProvider extends ActionProvider {

        private Context mContext;

        public ConfigActionProvider(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        public View onCreateActionView() {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.action_provider_config, null);

            Spinner spinner = (Spinner) view.findViewById(R.id.spinnerConfigActionProvider);
            // Create an ArrayAdapter using the string array and a default spinner layout
            final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                    R.array.currency_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);

            return view;
        }
    }
}



