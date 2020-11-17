package ru.besttuts.stockwidget.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import ru.besttuts.stockwidget.ui.EconomicWidget;
import ru.besttuts.stockwidget.util.LogUtils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;

public class AlfaMediaPlayerService extends Service {
    private static final String TAG = LogUtils.makeLogTag(AlfaMediaPlayerService.class);

    public static final String ACTION_PLAY = "ru.besttuts.stockwidget.service.AlfaMediaPlayerService.PLAY";
    public static final String ACTION_STOP = "ru.besttuts.stockwidget.service.AlfaMediaPlayerService.STOP";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_PLAY)) {
                LOGD(TAG, ACTION_PLAY);
//                if (mMediaPlayer == null) {
//                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bensound_anewbeginning); // initialize it here
//                    mMediaPlayer.start();
//                }

//                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        // Do something. For example: playButton.setEnabled(true);
//                    }
//                });
            } else if (intent.getAction().equals(ACTION_STOP)) {
                LOGD(TAG, ACTION_STOP);
                EconomicWidget.stopMusic(getApplicationContext());
//                if (mMediaPlayer != null) {
//                    mMediaPlayer.stop();
//                    mMediaPlayer.release();
//                    mMediaPlayer = null;
//                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
