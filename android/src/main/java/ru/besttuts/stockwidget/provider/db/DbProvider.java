package ru.besttuts.stockwidget.provider.db;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.VisibleForTesting;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.besttuts.stockwidget.model.QuoteLastTradeDate;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.provider.AppDatabase;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.db.impl.DbBackendAdapterImpl;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Setting;
import ru.besttuts.stockwidget.sync.MyFinanceWS;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 *         created on 1/24/2017.
 */

public class DbProvider {
    private static final String TAG = makeLogTag(DbProvider.class);

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    class CustomExecutor extends ThreadPoolExecutor {
        CustomExecutor() {
            super(NUMBER_OF_CORES, NUMBER_OF_CORES, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }

    private static DbProvider sDbProviderInstance;

    private Context mContext;

    private AppDatabase database;
    private final DbBackendAdapter mDbBackendAdapter;
    private final DbNotificationManager mDbNotificationManager;
    private final CustomExecutor mExecutor;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public interface ResultCallback<T> {
        void onFinished(T result);
    }

    private DbProvider(Context context) {
        mContext = context;
        database = AppDatabase.getInstance(context);
        mDbBackendAdapter = new DbBackendAdapterImpl(database);
        mDbNotificationManager = DbNotificationManager.getInstance();
        mExecutor = new CustomExecutor();
    }

    public static void init(Context context) {
        if (null == sDbProviderInstance) {
            sDbProviderInstance = new DbProvider(context);
        }
    }

    public static DbProvider getInstance() {
        if(null == sDbProviderInstance){
            throw new RuntimeException("before use call DbProvider.init(context) from Application.onCreate()");
        }
        return sDbProviderInstance;
    }

    @VisibleForTesting
    DbProvider(AppDatabase dbBackend,
               DbBackendAdapterImpl dbBackendAdapter,
               DbNotificationManager dbNotificationManager,
               CustomExecutor executor) {
        database = dbBackend;
        mDbBackendAdapter = dbBackendAdapter;
        mDbNotificationManager = dbNotificationManager;
        mExecutor = executor;
    }

    public List<Setting> getAllSettings(){
        return mDbBackendAdapter.getAllSettings();
    }

    public List<Setting> getAllSettingsWithCheck(){
        List<Setting> settings = getAllSettings();
//        syncQuotesWithLastTradeDate(settings);
        return settings;
    }

    public void addSettingsRec(final int mAppWidgetId, final int widgetItemPosition,
                               final int type, final String[] symbols){
        LOGD(TAG, String.format("addSettingsRec: %d %s", type, symbols));

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDbBackendAdapter.addSettingsRec(mAppWidgetId, widgetItemPosition, type, symbols);
                mDbNotificationManager.notifyListeners();
            }
        });
    }

    public boolean addModelRec(Model model) {
        database.modelDao().insertAll(model);
        return true;
    }

    public Model getModelById(String modelId){
        return mDbBackendAdapter.getModelById(modelId);
    }

    public List<Model> getModelsByWidgetId(final int widgetId) {
        return mDbBackendAdapter.getModelsByWidgetId(widgetId);
    }

    public void getModelsByWidgetId(final int widgetId, final ResultCallback<List<Model>> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<Model> state = mDbBackendAdapter.getModelsByWidgetId(widgetId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(state);
                    }
                });
            }
        });
    }

    public void getCursorSettingsWithModelByWidgetId(
            final int widgetId, final ResultCallback<Cursor> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = mDbBackend.getCursorSettingsWithModelByWidgetId(widgetId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(cursor);
                    }
                });
            }
        });
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Setting> settings = mDbBackendAdapter.getSettingsByWidgetId(widgetId);
//                syncQuotesWithLastTradeDate(settings);

                final Cursor cursor = mDbBackend.getCursorSettingsWithModelByWidgetId(widgetId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(cursor);
                    }
                });
            }
        });
    }

    public List<Setting> getCursorSettingsWithoutModelByWidgetId(int widgetId){
        return mDbBackendAdapter.getSettingsWithoutModelByWidgetId(widgetId);
    }

    public void deleteSettingsByWidgetId(final int widgetId){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDbBackendAdapter.deleteSettingsByWidgetId(widgetId);
                mDbNotificationManager.notifyListeners();
            }
        });
    }

    public void deleteSettingsByIdAndUpdatePositions(final String settingId, final int position) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDbBackendAdapter.deleteSettingsByIdAndUpdatePositions(settingId, position);
                mDbNotificationManager.notifyListeners();
            }
        });
    }

    public void deleteAll(){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDbBackendAdapter.deleteAll();
                mDbNotificationManager.notifyListeners();
            }
        });
    }

//    private synchronized void syncQuotesWithLastTradeDate(List<Setting> settings){
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//
//        long today = calendar.getTimeInMillis();
//
//        for (Setting setting: settings){
//            if (QuoteType.GOODS != setting.getQuoteType() || today < setting.getLastTradeDate()){
//                continue;
//            }
//
//            updateSettingWithNewSymbolAndLastTradeDate(setting, today);
//
//            database.settingDao().updateAll(setting);
//        }
//    }

//    private void updateSettingWithNewSymbolAndLastTradeDate(Setting setting, long today) {
//        String symbol = setting.getQuoteSymbol();
//        String code = symbol.substring(0, symbol.length() - 7);
//
//        Cursor cursor = mDbBackend.getCursorQuoteLastTradeDateForCurrentDay(code, today);
//
//        if (null == cursor || 0 == cursor.getCount()){
//            try {
//                MyFinanceWS ws = new MyFinanceWS(mContext);
//                List<QuoteLastTradeDate> quoteLastTradeDates = ws.getQuotesWithLastTradeDate();
//
//                mDbBackend.insertQuoteLastTradeDate(quoteLastTradeDates);
//
//                cursor = mDbBackend.getCursorQuoteLastTradeDateForCurrentDay(code, today);
//
//                if (null == cursor || 0 == cursor.getCount()) return;
//            } catch (IOException e) {
//                LOGE(TAG, e.getMessage());
//                return;
//            }
//        }
//
//        cursor.moveToFirst();
//
//        String newSymbol = cursor.getString(cursor.getColumnIndexOrThrow(
//                QuoteContract.QuoteLastTradeDateColumns.SYMBOL));
//        long newLastTradeDate = cursor.getLong(cursor.getColumnIndexOrThrow(
//                QuoteContract.QuoteLastTradeDateColumns.LAST_TRADE_DATE));
//
//        setting.setQuoteSymbol(newSymbol);
//        setting.setLastTradeDate(newLastTradeDate);
//
//        cursor.close();
//    }
}
