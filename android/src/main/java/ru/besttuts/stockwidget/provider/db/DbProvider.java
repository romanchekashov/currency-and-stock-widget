package ru.besttuts.stockwidget.provider.db;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.besttuts.stockwidget.model.Model;

/**
 * @author rchekashov
 *         created on 1/24/2017.
 */

public class DbProvider {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    class CustomExecutor extends ThreadPoolExecutor {
        CustomExecutor() {
            super(NUMBER_OF_CORES, NUMBER_OF_CORES, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }

    private static DbProvider sDbProviderInstance;

    private final DbBackend mDbBackend;
    private final DbBackendAdapter mDbBackendAdapter;
    private final DbNotificationManager mDbNotificationManager;
    private final CustomExecutor mExecutor;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public interface ResultCallback<T> {
        void onFinished(T result);
    }

    private DbProvider(Context context) {
        mDbBackend = new DbBackend(context);
        mDbBackendAdapter = new DbBackendAdapter(mDbBackend);
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
    DbProvider(DbBackend dbBackend,
               DbBackendAdapter dbBackendAdapter,
               DbNotificationManager dbNotificationManager,
               CustomExecutor executor) {
        mDbBackend = dbBackend;
        mDbBackendAdapter = dbBackendAdapter;
        mDbNotificationManager = dbNotificationManager;
        mExecutor = executor;
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
    }
}
