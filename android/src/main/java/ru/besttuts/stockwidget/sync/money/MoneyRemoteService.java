package ru.besttuts.stockwidget.sync.money;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

import android.content.Context;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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

    private static final String MY_FINANCE_BASE_URL = "https://money.look.ovh";
    private static final String PREF_KEY_LAST_CALL_TIME_TICKER_SYMBOLS = "MoneyRemoteService_tickerSymbols_lastCallTime";
    private static final String PREF_KEY_LAST_CALL_TIME_TICKER_TAPE = "MoneyRemoteService_tickerTape_lastCallTime";

    private Context mContext;

    public MoneyRemoteService(Context context) {
        LOGD(TAG, "constructor called: " + this);
        mContext = context;
    }

    public TickerSymbolsDto tickerSymbols() throws IOException {
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        long lastCallTime = (long) SharedPreferencesHelper.get(PREF_KEY_LAST_CALL_TIME_TICKER_SYMBOLS, 0L, mContext);
        boolean needRefresh = currentTimeInMillis - lastCallTime > TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);

        LOGD(TAG, String.format("[tickerSymbols]: lastCallTime = %s, needRefresh = %s", lastCallTime, needRefresh));

        if (needRefresh) {
            SharedPreferencesHelper.update(PREF_KEY_LAST_CALL_TIME_TICKER_SYMBOLS, currentTimeInMillis, mContext);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MY_FINANCE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MoneyRetrofitService service = retrofit.create(MoneyRetrofitService.class);

            TickerSymbolsDto symbolsDto = service.tickerSymbols().execute().body();

            LOGD(TAG, "[tickerSymbols]: TickerSymbolsDto fetched = " + symbolsDto);

            return symbolsDto;
        }

        return new TickerSymbolsDto();
    }

    public List<QuoteDto> tickerTape(TickerFilterDto filter) throws IOException {
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        long lastCallTime = (long) SharedPreferencesHelper.get(PREF_KEY_LAST_CALL_TIME_TICKER_TAPE, 0L, mContext);
        boolean needRefresh = currentTimeInMillis - lastCallTime > TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);

        LOGD(TAG, String.format("[tickerTape]: lastCallTime = %s, needRefresh = %s", lastCallTime, needRefresh));

        if (needRefresh) {
            SharedPreferencesHelper.update(PREF_KEY_LAST_CALL_TIME_TICKER_TAPE, currentTimeInMillis, mContext);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MY_FINANCE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MoneyRetrofitService service = retrofit.create(MoneyRetrofitService.class);

            List<QuoteDto> quotes = service.tickerTape(filter).execute().body();
            if (quotes == null) quotes = Collections.emptyList();

            LOGD(TAG, "[tickerTape]: quotes fetched = " + quotes.size());

            return quotes;
        }

        return Collections.emptyList();
    }
}
