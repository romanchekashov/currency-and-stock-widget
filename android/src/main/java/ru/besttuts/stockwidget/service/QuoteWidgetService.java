package ru.besttuts.stockwidget.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.widget.RemoteViewsService;

import ru.besttuts.stockwidget.provider.QuoteRemoteViewsFactory;

/**
 * Created by roman on 22.01.2015.
 */
@SuppressLint("NewApi")
public class QuoteWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new QuoteRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
