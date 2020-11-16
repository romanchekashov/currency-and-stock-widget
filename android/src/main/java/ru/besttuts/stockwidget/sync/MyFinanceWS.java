package ru.besttuts.stockwidget.sync;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.besttuts.stockwidget.provider.model.QuoteType;
import ru.besttuts.stockwidget.sync.sparklab.dto.InitDataDto;
import ru.besttuts.stockwidget.sync.sparklab.dto.MobileQuoteFilter;
import ru.besttuts.stockwidget.sync.sparklab.dto.MobileQuoteShort;
import ru.besttuts.stockwidget.sync.sparklab.dto.QuoteDto;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 * created on 10/7/2016.
 */

public class MyFinanceWS {
    private static final String TAG = makeLogTag(MyFinanceWS.class);

    private static final String MY_FINANCE_BASE_URL = "https://trader-public.herokuapp.com";
    private static final String PREF_KEY_LAST_CALL_TIME = "MyFinanceWS_getQuotesWithLastTradeDate_lastCallTime";

    private Context mContext;

    public MyFinanceWS(Context context) {
        LOGD(TAG, "constructor called: " + this);
        mContext = context;
    }

    public List<MobileQuoteShort> getQuotes(MobileQuoteFilter filter) throws IOException {
//        return testData(symbols);
        Call<List<MobileQuoteShort>> call = filter != null ? getService().quotes(filter) : getService().quotes();
        List<MobileQuoteShort> quotes = call.execute().body();
        LOGD(TAG, "[getQuotes]: quotes fetched = " + quotes.size());
        return quotes;
    }

    public InitDataDto getSupportedData() {
        InitDataDto dto = new InitDataDto();
        return dto;
    }

    private MyFinanceService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MY_FINANCE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MyFinanceService.class);
    }

    private List<QuoteDto> testData(List<String> symbols) {
        List<QuoteDto> dtos = new ArrayList<>();
        for (String s : symbols) {
            QuoteDto dto = new QuoteDto();
            dto.setSymbol(s);
            dto.setRate(60.5);
            dto.setChange(0.5);
            dto.setName(s + "Name");
            dto.setCurrency("USD");
            dto.setTimestamp(System.currentTimeMillis());
            dto.setType(QuoteType.CURRENCY);
            dtos.add(dto);
        }

        if (symbols != null && !symbols.isEmpty()) {
            dtos.get(0).setProvider("ALFA_BANK");
        }
        return dtos;
    }
}
