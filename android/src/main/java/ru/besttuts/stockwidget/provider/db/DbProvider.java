package ru.besttuts.stockwidget.provider.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.VisibleForTesting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.besttuts.stockwidget.provider.AppDatabase;
import ru.besttuts.stockwidget.provider.dao.ModelDao;
import ru.besttuts.stockwidget.provider.dao.QuoteDao;
import ru.besttuts.stockwidget.provider.dao.QuoteProviderDao;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Quote;
import ru.besttuts.stockwidget.provider.model.Setting;
import ru.besttuts.stockwidget.provider.model.wrap.ModelSetting;
import ru.besttuts.stockwidget.sync.MyFinanceWS;
import ru.besttuts.stockwidget.sync.sparklab.dto.MobileQuoteShort;
import ru.besttuts.stockwidget.util.CustomConverter;
import ru.besttuts.stockwidget.util.SharedPreferencesHelper;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 * created on 1/24/2017.
 */

public class DbProvider extends DbProviderAbs {

    public interface ResultCallback<T> {
        void onFinished(T result);
    }

    private DbProvider(Context context) {
        super(context);
    }

    @VisibleForTesting
    DbProvider(AppDatabase dbBackend,
               DbBackendAdapterImpl dbBackendAdapter,
               CustomExecutor executor) {
        super(dbBackend, dbBackendAdapter, executor);
    }

    public static void init(Context context) {
        if (null == sDbProviderInstance) {
            sDbProviderInstance = new DbProvider(context);
        }
    }

    public static DbProvider getInstance() {
        if (null == sDbProviderInstance) {
            throw new RuntimeException("before use call DbProvider.init(context) from Application.onCreate()");
        }
        return sDbProviderInstance;
    }

    public static AppDatabase getDatabase() {
        return getInstance().database;
    }

    public static ModelDao modelDao() {
        return getDatabase().modelDao();
    }

    public static QuoteDao quoteDao() {
        return getDatabase().quoteDao();
    }

    public static QuoteProviderDao quoteProviderDao() {
        return getDatabase().quoteProviderDao();
    }

    public List<Setting> getAllSettings() {
        return mDbBackendAdapter.getAllSettings();
    }

    public List<Setting> getAllSettingsWithCheck() {
        List<Setting> settings = getAllSettings();
//        syncQuotesWithLastTradeDate(settings);
        return settings;
    }

    public boolean addModelRec(Model model) {
        database.modelDao().insertAll(model);
        return true;
    }

    public Model getModelById(String modelId) {
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

    public void getSettingsWithModelByWidgetId(final int widgetId,
                                               final ResultCallback<List<ModelSetting>> callback) {
        mExecutor.execute(() -> {
            List<ModelSetting> modelSettings = mDbBackendAdapter
                    .getSettingsWithModelByWidgetId(widgetId);
            if (modelSettings.isEmpty()) {
                List<Setting> settings = mDbBackendAdapter.getSettingsByWidgetId(widgetId);
                List<String> symbols = new ArrayList<>(settings.size());
                for (Setting setting : settings) symbols.add(setting.getQuoteSymbol());

                try {
                    List<MobileQuoteShort> quoteDtos = new MyFinanceWS(mContext)
                            .getQuotes(SharedPreferencesHelper.getMobileQuoteFilter(mContext));
                    for (MobileQuoteShort dto : quoteDtos) {
                        database.modelDao().insertAll(CustomConverter.toModel(dto));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                modelSettings = mDbBackendAdapter
                        .getSettingsWithModelByWidgetId(widgetId);
            }

            final List<ModelSetting> modelSettingsFinal = modelSettings;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFinished(modelSettingsFinal);
                }
            });
        });
//        mExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                List<Setting> settings = mDbBackendAdapter.getSettingsByWidgetId(widgetId);
//                syncQuotesWithLastTradeDate(settings);
//
//                final Cursor cursor = mDbBackend.getCursorSettingsWithModelByWidgetId(widgetId);
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        callback.onFinished(cursor);
//                    }
//                });
//            }
//        });
    }

    public List<Setting> getCursorSettingsWithoutModelByWidgetId(int widgetId) {
        return mDbBackendAdapter.getSettingsWithoutModelByWidgetId(widgetId);
    }

    public void deleteSettingsByWidgetId(final int widgetId) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDbBackendAdapter.deleteSettingsByWidgetId(widgetId);
            }
        });
    }

    public void deleteSettingsByIdAndUpdatePositions(final String settingId,
                                                     final int position) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDbBackendAdapter.deleteSettingsByIdAndUpdatePositions(settingId, position);
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
//            if (QuoteType.COMMODITY != setting.getQuoteType() || today < setting.getLastTradeDate()){
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
//
//        setting.setQuoteSymbol(newSymbol);
//        setting.setLastTradeDate(newLastTradeDate);
//
//        cursor.close();
//    }

    public boolean saveQuotes(List<MobileQuoteShort> quoteShorts) {
        Quote[] quotes = new Quote[quoteShorts.size()];
        int i = 0;
        for (MobileQuoteShort q : quoteShorts) {
            Quote quote = new Quote();
            quote.setQuoteSymbol(q.getS());
            quote.setQuoteName(q.getN());
            quote.setQuoteType(String.valueOf(q.getQt()));
            quotes[i++] = quote;
        }
        quoteDao().insertAll(quotes);
        return true;
    }
}
