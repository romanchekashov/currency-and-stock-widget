package ru.besttuts.stockwidget.provider.db;

import android.os.Handler;
import android.os.Looper;

import java.util.HashSet;

/**
 * Created by user on 31/07/2016.
 */
public class DbNotificationManager {
    private static DbNotificationManager sDbNotificationInstance;

    private DbNotificationManager(){}

    public static DbNotificationManager getInstance() {
        if (null == sDbNotificationInstance) {
            sDbNotificationInstance = new DbNotificationManager();
        }
        return sDbNotificationInstance;
    }

    private HashSet<Listener> mListeners = new HashSet<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mNotifyRunnable = new Runnable() {
        @Override
        public void run() {
            notifyOnUiThread();
        }
    };

    public interface Listener {
        void onDataUpdated();
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    /* package-private */ void notifyListeners() {
        mHandler.removeCallbacks(mNotifyRunnable);
        mHandler.post(mNotifyRunnable);
    }

    private void notifyOnUiThread() {
        for (Listener l : mListeners) l.onDataUpdated();
    }
}
