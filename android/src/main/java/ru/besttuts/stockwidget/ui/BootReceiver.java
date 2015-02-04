package ru.besttuts.stockwidget.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            EconomicWidget.setAlarm(context);
        }
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
