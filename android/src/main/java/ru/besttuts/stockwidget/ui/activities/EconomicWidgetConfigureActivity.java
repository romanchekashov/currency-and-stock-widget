package ru.besttuts.stockwidget.ui.activities;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.besttuts.stockwidget.Config;
import ru.besttuts.stockwidget.PrivateConstants;
import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.service.UpdateService;
import ru.besttuts.stockwidget.ui.App;
import ru.besttuts.stockwidget.ui.EconomicWidget;
import ru.besttuts.stockwidget.ui.fragments.ConfigPreferenceFragment;
import ru.besttuts.stockwidget.ui.fragments.SlidingTabsFragment;
import ru.besttuts.stockwidget.ui.fragments.TrackingQuotesFragment;
import ru.besttuts.stockwidget.util.AppRater;
import ru.besttuts.stockwidget.util.NotificationManager;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;


/**
 * The configuration screen for the {@link EconomicWidget EconomicWidget} AppWidget.
 */
public class EconomicWidgetConfigureActivity extends AppCompatActivity
        implements SlidingTabsFragment.OnFragmentInteractionListener,
        TrackingQuotesFragment.OnFragmentInteractionListener,
        NotificationManager.ColorChangedListener {

    private static final String TAG = makeLogTag(EconomicWidgetConfigureActivity.class);


    /** Your ad unit id. Replace with your actual ad unit id. */
    private static final String BANNER_AD_UNIT_ID = PrivateConstants.BANNER_AD_UNIT_ID;
//            Config.getProperty("ru.besttuts.stockwidget.PrivateConstants", "BANNER_AD_UNIT_ID");
    private static final String INTERSTITIAL_AD_UNIT_ID = PrivateConstants.INTERSTITIAL_AD_UNIT_ID;
//            Config.getProperty("ru.besttuts.stockwidget.PrivateConstants", "INTERSTITIAL_AD_UNIT_ID");
    private static final String HASHED_DEVICE_ID = PrivateConstants.HASHED_DEVICE_ID;
//            Config.getProperty("ru.besttuts.stockwidget.PrivateConstants", "HASHED_DEVICE_ID");

    /** The interstitial ad. */
    private InterstitialAd interstitialAd;

    public static final String ARG_WIDGET_ID = "widgetId";
    public static final String ARG_QUOTE_TYPE_VALUE = "quoteTypeValue";
    public static final String ARG_WIDGET_ITEM_POSITION = "widgetItemPosition";
    static final String STATE_POSITION = "position";

    public static final String PREFS_NAME = "ru.besttuts.stockwidget.ui.EconomicWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    public static QuoteDataSource mDataSource;

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

        // при выходе засчитываем один запуск
        AppRater.countLaunches(this);

        showInterstitial();

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

        // Создаем рекламный баннер
        createAds();

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

            // Проверяем нужно ли показать диалог оценки приложения
            AppRater.app_launched(this, getSupportFragmentManager());

        } else {
            position = savedInstanceState.getInt(STATE_POSITION);
        }

        // создаем объект для создания и управления версиями БД
        mDataSource = new QuoteDataSource(this);

//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        LOGD(TAG, String.format("onCreate: density = %f, densityDpi = %d, heightPixels = %d, " +
//                        "widthPixels = %d, scaledDensity = %f, xdpi = %f, ydpi = %f",
//                metrics.density, metrics.densityDpi, metrics.heightPixels, metrics.widthPixels,
//                metrics.scaledDensity, metrics.xdpi, metrics.ydpi));

//        if (BuildConfig.DEBUG) {
//            File file = new File("/data/data/ru.besttuts.stockwidget");
//            SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
//            LOGD(TAG, "PreferenceManager.getDefaultSharedPreferences: " + sp);
//            Map<String, ?> map = sp.getAll();
//            Set<String> keys = map.keySet();
//            for (String key: keys) {
//                LOGD(TAG, String.format("%s: %s", key, map.get(key)));
//            }
//
//            sp = PreferenceManager.getDefaultSharedPreferences(this);
//            LOGD(TAG, "PreferenceManager.getDefaultSharedPreferences: " + sp);
//            map = sp.getAll();
//            keys = map.keySet();
//            for (String key: keys) {
//                LOGD(TAG, String.format("%s: %s", key, map.get(key)));
//            }
//        }
        //Получаю треккер отслеживания для Гугл-Аналитики (должен автоматически отправлять отчеты)
        if(Config.IS_DEV_MODE){
            ((App) getApplication()).getTracker(App.TrackerName.APP_TRACKER);
        }

        LOGD(TAG, "[onCreate]: complete");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, position);

        super.onSaveInstanceState(outState);
    }

    public void createAds() {
        if(Config.IS_DEV_MODE){
            LOGD(TAG, "[createAds]: NO ADS cause in DEV mode!");
            return;
        }
        LOGD(TAG, "[createAds]: creating...");

        // Создание экземпляра adView.
        AdView adView = new AdView(this);
        adView.setAdUnitId(BANNER_AD_UNIT_ID);
        adView.setAdSize(AdSize.SMART_BANNER);

        // Поиск разметки LinearLayout (предполагается, что ей был присвоен
        // атрибут android:id="@+id/mainLayout").
        FrameLayout adViewFrameLayout = (FrameLayout)findViewById(R.id.adViewFrameLayout);

        // Добавление в разметку экземпляра adView.
        adViewFrameLayout.addView(adView);

        // Инициирование общего запроса.
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice(HASHED_DEVICE_ID)
                .build();

        // Загрузка adView с объявлением.
        adView.loadAd(adRequest);

        // Create an ad.
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);
        // Set the AdListener.
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                LOGD(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
                LOGD(TAG, message);
            }
        });

        // Check the logcat output for your hashed device ID to get test ads on a physical device.
        AdRequest interstitialAdRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice(HASHED_DEVICE_ID)
                .build();

        // Load the interstitial ad.
        interstitialAd.loadAd(interstitialAdRequest);
    }

    /** Called when the Show Interstitial button is clicked. */
    public void showInterstitial() {
        if(Config.IS_DEV_MODE){
            return;
        }

        if (null == interstitialAd) {
            LOGE(TAG, "interstitialAd = " + interstitialAd);
            return;
        }
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            LOGD(TAG, "Interstitial ad was not ready to be shown.");
        }
    }

    /** Gets a string error reason from an error code. */
    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch(errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // при выходе засчитываем один запуск
        AppRater.countLaunches(this);
        showInterstitial();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Config.IS_DEV_MODE){
            //Get an Analytics tracker to report app starts & uncaught exceptions etc.
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LOGD(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Config.IS_DEV_MODE){
            //Stop the analytics tracking
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mDataSource) mDataSource.close();
        NotificationManager.removeListener(this);
        LOGD(TAG, "onDestroy");
    }

    public static void saveWidgetLayoutPref(Context context, int appWidgetId, int layout) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId + "_widget_layout", layout);
        prefs.commit();
    }

    public static int loadWidgetLayoutPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId + "_widget_layout", R.layout.economic_widget);
    }

    public static void deleteWidgetLayoutPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_widget_layout");
        prefs.commit();
    }

    public static void saveWidgetLayoutGridItemPref(Context context, int appWidgetId, int layoutGridItem) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId + "_widget_layout_grid_item", layoutGridItem);
        prefs.commit();
    }

    public static int loadWidgetLayoutGridItemPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId + "_widget_layout_grid_item", R.layout.economic_widget_item);
    }

    public static void deleteWidgetLayoutGridItemPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_widget_layout_grid_item");
        prefs.commit();
    }

    public static void saveLastUpdateTimePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_lastupdatetime", text);
        prefs.commit();
    }

    public static String loadLastUpdateTimePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_lastupdatetime", null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return new SimpleDateFormat().format(Calendar.getInstance().getTime());
        }
    }

    public static void deleteLastUpdateTimePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_lastupdatetime");
        prefs.commit();
    }

    public static int getColor(Context context, boolean hasAlpha) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String sColor = sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE);
        if (!hasAlpha) return Color.parseColor(sColor);

        String sBgColor = "#D0" + sColor.substring(1);

        return Color.parseColor(sBgColor);
    }

    @Override
    public void changeColor() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sColor = sharedPref.getString(ConfigPreferenceFragment.KEY_PREF_BG_COLOR,
                ConfigPreferenceFragment.KEY_PREF_BG_COLOR_DEFAULT_VALUE);
        String sBgColor = "#D0" + sColor.substring(1);

        int color = Color.parseColor(sBgColor);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));

        findViewById(R.id.adViewFrameLayout).setBackgroundColor(color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            color = Color.parseColor(sColor);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(color);
        }
    }

}



