package ru.besttuts.stockwidget.sync;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.besttuts.stockwidget.model.QuoteLastTradeDate;
import ru.besttuts.stockwidget.sync.sparklab.QuoteDto;
import ru.besttuts.stockwidget.util.SharedPreferencesHelper;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 *         created on 10/7/2016.
 */

public class MyFinanceWS {
    private static final String TAG = makeLogTag(MyFinanceWS.class);

//    private static final String MY_FINANCE_BASE_URL = "http://10.0.2.2:3001/"; //TODO: change to real server url
    private static final String MY_FINANCE_BASE_URL = "http://finance.besttuts.ru/";
    private static final String PREF_KEY_LAST_CALL_TIME = "MyFinanceWS_getQuotesWithLastTradeDate_lastCallTime";

    private Context mContext;

    public MyFinanceWS(Context context) {
        LOGD(TAG, "constructor called: " + this);
        mContext = context;
    }

    public List<QuoteLastTradeDate> getQuotesWithLastTradeDate() throws IOException {
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        long lastCallTime = (long) SharedPreferencesHelper.get(PREF_KEY_LAST_CALL_TIME, 0L, mContext);

        LOGD(TAG, "[getQuotesWithLastTradeDate]: lastCallTime = " + lastCallTime);
        if(currentTimeInMillis - lastCallTime > TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)){
            SharedPreferencesHelper.update(PREF_KEY_LAST_CALL_TIME, currentTimeInMillis, mContext);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MY_FINANCE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MyFinanceService service = retrofit.create(MyFinanceService.class);

            List<QuoteLastTradeDate> quotes = service.quotesWithLastTradeDate().execute().body();
            LOGD(TAG, "[getQuotesWithLastTradeDate]: quotes fetched = " + quotes.size());

            return quotes;
        }

        return new ArrayList<>();
    }

    public List<QuoteDto> getQuotes(List<String> symbols) throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MY_FINANCE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyFinanceService service = retrofit.create(MyFinanceService.class);

        StringBuilder builder = new StringBuilder();
        for (String s: symbols) {
            builder.append(s).append(",");
        }

        String sSymbols = builder.substring(0, builder.length() - 1);
        List<QuoteDto> quotes = service.quotes(sSymbols).execute().body();
        LOGD(TAG, "[getQuotes]: quotes fetched = " + quotes.size());

        return quotes;
    }
}
