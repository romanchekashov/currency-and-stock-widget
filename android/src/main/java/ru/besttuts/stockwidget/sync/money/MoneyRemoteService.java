package ru.besttuts.stockwidget.sync.money;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.besttuts.stockwidget.model.QuoteLastTradeDate;
import ru.besttuts.stockwidget.sync.MyFinanceService;
import ru.besttuts.stockwidget.sync.money.dto.QuoteDto;
import ru.besttuts.stockwidget.sync.money.dto.TickerFilterDto;
import ru.besttuts.stockwidget.sync.money.dto.TickerSymbolsDto;
import ru.besttuts.stockwidget.util.SharedPreferencesHelper;

/**
 * @author rchekashov
 *         created on 10/7/2016.
 */

public class MoneyRemoteService {
    private static final String TAG = makeLogTag(MoneyRemoteService.class);

    private static final String MY_FINANCE_BASE_URL = "http://money.look.ovh";
    private static final String PREF_KEY_LAST_CALL_TIME = "MyFinanceWS_getQuotesWithLastTradeDate_lastCallTime";

    private Context mContext;

    public MoneyRemoteService(Context context) {
        LOGD(TAG, "constructor called: " + this);
        mContext = context;
    }

    public TickerSymbolsDto tickerSymbols() throws IOException {
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        long lastCallTime = (long) SharedPreferencesHelper.get(PREF_KEY_LAST_CALL_TIME, 0L, mContext);

        LOGD(TAG, "[getQuotesWithLastTradeDate]: lastCallTime = " + lastCallTime);

        if (currentTimeInMillis - lastCallTime > TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)) {
            SharedPreferencesHelper.update(PREF_KEY_LAST_CALL_TIME, currentTimeInMillis, mContext);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MY_FINANCE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MoneyRetrofitService service = retrofit.create(MoneyRetrofitService.class);

            TickerSymbolsDto symbolsDto = service.tickerSymbols().execute().body();

            LOGD(TAG, "[getQuotesWithLastTradeDate]: TickerSymbolsDto fetched = " + symbolsDto);

            return symbolsDto;
        }

        return new TickerSymbolsDto();
    }

    public List<QuoteDto> tickerTape(TickerFilterDto filter) throws IOException {
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        long lastCallTime = (long) SharedPreferencesHelper.get(PREF_KEY_LAST_CALL_TIME, 0L, mContext);

        LOGD(TAG, "[getQuotesWithLastTradeDate]: lastCallTime = " + lastCallTime);

        if (currentTimeInMillis - lastCallTime > TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)) {
            SharedPreferencesHelper.update(PREF_KEY_LAST_CALL_TIME, currentTimeInMillis, mContext);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MY_FINANCE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MoneyRetrofitService service = retrofit.create(MoneyRetrofitService.class);

            List<QuoteDto> quotes = service.tickerTape(filter).execute().body();

            LOGD(TAG, "[getQuotesWithLastTradeDate]: quotes fetched = " + quotes);

            return quotes;
        }

        return Collections.emptyList();
    }
}
