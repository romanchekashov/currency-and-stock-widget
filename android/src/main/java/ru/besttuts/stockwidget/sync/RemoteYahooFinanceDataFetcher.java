package ru.besttuts.stockwidget.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;
import ru.besttuts.stockwidget.sync.deserializer.YahooMultiQueryDataDeserializer;
import ru.besttuts.stockwidget.sync.model.YahooMultiQueryData;
import ru.besttuts.stockwidget.util.Utils;
import ru.besttuts.stockwidget.util.YahooQueryBuilder;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 07.01.2015.
 */
public class RemoteYahooFinanceDataFetcher {
    private static final String TAG = makeLogTag(RemoteYahooFinanceDataFetcher.class);

    private String xchangeUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('EURUSD'%2C'USDRUB'%2C'EURRUB'%2C'CNYRUB')%3B&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

    private String quotesUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22GCF15.CMX%22%2C%22PLF15.NYM%22%2C%22PAF15.NYM%22%2C%22SIF15.CMX%22%2C%22HGF15.CMX%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

    private String baseYahooUrlreturnJsonPrepand = "https://query.yahooapis.com/v1/public/yql?q=";
    private String baseYahooUrlreturnJsonAppend = "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

    private String yahooFinanceMultiQuery = "SELECT%20*%20FROM%20query.multi%20WHERE%20queries%3D%22%s%3B%22";

    private String yahooFinanceXchangeQueryUrl = "select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(";

    private String yahooFinanceQuotesQueryUrl = "select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(";

    private Set<String> currencyExchangeSet = new HashSet<>();
    private Set<String> goodSet = new HashSet<>();

    public void populateQuoteSet(int quoteType, String symbol) {
        switch (quoteType) {
            case QuoteType.CURRENCY:
                currencyExchangeSet.add(symbol);
                break;
            case QuoteType.GOODS:
            case QuoteType.INDICES:
            case QuoteType.STOCK:
            case QuoteType.QUOTES:
//                String s = new String(symbol);
//                if ("^DJI".equals(s)) s = "INDU"; // символ исключение для ^DJI
                goodSet.add(symbol);
                break;
        }
    }

    public void populateQuoteSet(List<Setting> settings) {
        for (Setting setting : settings) {
            switch (setting.getQuoteType()) {
                case QuoteType.CURRENCY:
                    currencyExchangeSet.add(setting.getQuoteSymbol());
                    break;
                case QuoteType.GOODS:
                case QuoteType.INDICES:
                case QuoteType.STOCK:
                case QuoteType.QUOTES:
//                    String s = new String(setting.getQuoteSymbol());
//                    if ("^DJI".equals(s)) s = "INDU"; // символ исключение для ^DJI
                    goodSet.add(setting.getQuoteSymbol());
                    break;
            }
        }
    }

    public String buildYahooFinanceMultiQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT%20*%20FROM%20query.multi%20WHERE%20queries%3D%22");
        if (null != currencyExchangeSet && currencyExchangeSet.size() > 0) {
            builder.append(getYahooFinanceXchangeQuery());
        }
        builder.append(";");
        if (null != goodSet && goodSet.size() > 0) {
            builder.append(getYahooFinanceQuotesQuery());
        }
        builder.append("%3B%22");

        return builder.toString();
    }

    public String buildYahooFinanceMultiQueryUrl() {
        return baseYahooUrlreturnJsonPrepand + buildYahooFinanceMultiQuery() + baseYahooUrlreturnJsonAppend;
    }

    public String transformCurrencyExchangeSetToString(){
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        for (String s : currencyExchangeSet) {
            builder.append(s);
            builder.append("'%2C'");
        }
        return builder.substring(0, builder.length() - 4);
    }

    public String getYahooFinanceXchangeQuery() {
        return yahooFinanceXchangeQueryUrl + transformCurrencyExchangeSetToString() + ")";
    }

    public String transformQuoteSetToString(){
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        for (String s : goodSet) {
            builder.append(s);
            builder.append("'%2C'");
        }
        return builder.substring(0, builder.length() - 4);
    }

    public String getYahooFinanceQuotesQuery() {
        return yahooFinanceQuotesQueryUrl + transformQuoteSetToString() + ")";
    }

    // Convert the InputStream into a string
    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public String downloadQuotes() throws IOException {
        String sUrl = buildYahooFinanceMultiQueryUrl();
        LOGD(TAG, "downloadQuotes: " + sUrl);
        return downloadUrl(sUrl);
    }

    public YahooMultiQueryData getYahooMultiQueryData() throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(
                YahooMultiQueryData.class, new YahooMultiQueryDataDeserializer())
                .create();

//        OkHttpClient client = new OkHttpClient.Builder()
//                .addNetworkInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request request = chain.request();
//                        Request newRequest;
//                        String newUrl = YahooQueryBuilder.HTTP_QUERY_YAHOOAPIS_COM_V1_PUBLIC + "yql?"
//                                + Utils.encodeYahooApiQuery(request.url().query());
//                        LOGD(TAG, "request.url.query: " + newUrl);
//                        newRequest = request.newBuilder().url(newUrl).build();
//                        return chain.proceed(newRequest);
//                    }
//                })
//                .build();

        Retrofit retrofit = new Retrofit.Builder()
//                .client(client)
                .baseUrl(YahooQueryBuilder.HTTP_QUERY_YAHOOAPIS_COM_V1_PUBLIC)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        YahooFinanceService service = retrofit.create(YahooFinanceService.class);

        String yahooQuery = YahooQueryBuilder.buildYahooFinanceMultiQuery(currencyExchangeSet, goodSet);
//        String encodedYahooQuery = Utils.encodeYahooApiQuery(yahooQuery);

        Call<YahooMultiQueryData> callYahooMultiQueryData = service.yahooMultiQueryData(yahooQuery);

        LOGD(TAG, "(getYahooMultiQueryData): " + callYahooMultiQueryData.request().url());

        YahooMultiQueryData data = callYahooMultiQueryData.execute().body();

        if(null == data) {
            LOGE(TAG, "[FAIL] load or parse data from yahoo api: YahooMultiQueryData = " + data);
//            String newUrl = YahooQueryBuilder.HTTP_QUERY_YAHOOAPIS_COM_V1_PUBLIC + "yql?" + encodedYahooQuery;
//            LOGD(TAG, "[SUCCESS] load: " + downloadUrl(newUrl));
            return new YahooMultiQueryData();
        }

        LOGD(TAG, "[SUCCESS] load or parse data from yahoo api: YahooMultiQueryData = " + data);
        return data;
    }

    public String downloadUrl(String sUrl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(sUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            LOGD(TAG, "The response is: " + response);
            is = conn.getInputStream();

            return convertStreamToString(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}
