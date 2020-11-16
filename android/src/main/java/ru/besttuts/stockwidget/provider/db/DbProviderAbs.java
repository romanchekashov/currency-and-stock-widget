package ru.besttuts.stockwidget.provider.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.besttuts.stockwidget.provider.AppDatabase;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public abstract class DbProviderAbs implements DbProviderFunctions {
    protected static final String TAG = makeLogTag(DbProvider.class);

    protected static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    class CustomExecutor extends ThreadPoolExecutor {
        CustomExecutor() {
            super(NUMBER_OF_CORES, NUMBER_OF_CORES, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }

    protected static DbProvider sDbProviderInstance;

    protected Context mContext;

    protected AppDatabase database;
    protected DbBackendAdapter mDbBackendAdapter;
    protected CustomExecutor mExecutor;
    protected Handler mHandler = new Handler(Looper.getMainLooper());

    protected DbProviderAbs(Context context) {
        mContext = context;
        database = AppDatabase.getInstance(context);
        mDbBackendAdapter = new DbBackendAdapterImpl(database);
        mExecutor = new CustomExecutor();
    }

    protected DbProviderAbs(AppDatabase dbBackend,
                         DbBackendAdapterImpl dbBackendAdapter,
                         CustomExecutor executor) {
        database = dbBackend;
        mDbBackendAdapter = dbBackendAdapter;
        mExecutor = executor;
    }

    public DbBackendAdapter getDatabaseAdapter() {
        return mDbBackendAdapter;
    }
}
