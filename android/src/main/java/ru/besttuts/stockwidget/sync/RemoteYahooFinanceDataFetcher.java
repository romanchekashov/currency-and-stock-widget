package ru.besttuts.stockwidget.sync;

import android.util.Log;

import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.model.Setting;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by roman on 07.01.2015.
 */
public class RemoteYahooFinanceDataFetcher {

    final String LOG_TAG = "EconomicWidget.RemoteYahooFinanceDataFetcher";

    private String baseUrl = "http://query.yahooapis.com/v1/public/yql?q=";

    private String multiQuery = "SELECT * FROM query.multi WHERE queries = \"%s\"";

    private String exchangeQueryUrl = "select * from yahoo.finance.xchange where pair in (%s);";

    private String quotesQueryUrl = "select * from yahoo.finance.quotes where symbol in (%s);";


    public String createExchangeQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        for (String s: currencyExchangeSet) {
            builder.append(s);
            builder.append("','");
        }
        return String.format(exchangeQueryUrl, builder.substring(0, builder.length() - 2));
    }

    public String createQuotesQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        for (String s: goodSet) {
            builder.append(s);
            builder.append("','");
        }
        return String.format(quotesQueryUrl, builder.substring(0, builder.length() - 2));
    }

    public String createMultiQuery() {
        StringBuilder builder = new StringBuilder();
        if (null != currencyExchangeSet && currencyExchangeSet.size() > 0) {
            builder.append(createExchangeQuery());
        }
        if (null != goodSet && goodSet.size() > 0) {
            builder.append(createQuotesQuery());
        }

        return String.format(multiQuery, builder.toString());
    }

    public String createMultiQueryUrl() throws UnsupportedEncodingException {
        return baseUrl + URLEncoder.encode(createMultiQuery(), "UTF-8") + "&format=json";
    }

    private String xchangeUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('EURUSD'%2C'USDRUB'%2C'EURRUB'%2C'CNYRUB')%3B&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

    private String quotesUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22GCF15.CMX%22%2C%22PLF15.NYM%22%2C%22PAF15.NYM%22%2C%22SIF15.CMX%22%2C%22HGF15.CMX%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

    private String baseYahooUrlreturnJsonPrepand = "https://query.yahooapis.com/v1/public/yql?q=";
    private String baseYahooUrlreturnJsonAppend = "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

    private String yahooFinanceMultiQuery = "SELECT%20*%20FROM%20query.multi%20WHERE%20queries%3D%22%s%3B%22";

    private String yahooFinanceXchangeQueryUrl = "select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(";

    private String yahooFinanceQuotesQueryUrl = "select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(";

    private Set<String> currencyExchangeSet = new HashSet<>();
    private Set<String> goodSet = new HashSet<>();

    private String[] exchange = new String[]{"EURUSD","USDRUB","EURRUB","CNYRUB"};
    private String[] goods = new String[]{"GCF15.CMX","PLF15.NYM","PAF15.NYM","SIF15.CMX","HGF15.CMX"};

//    public String getYahooFinanceMultiQueryUrl() {
//        return String.format(baseYahooUrlreturnJson, getYahooFinanceXchangeQuery());
//    }

    public void populateQuoteSet(QuoteType type, String symbol) {
        switch (type) {
            case CURRENCY_EXCHANGE:
                currencyExchangeSet.add(symbol);
                break;
            case GOODS:
                goodSet.add(symbol);
                break;
        }
    }

    public void populateQuoteSet(List<Setting> settings) {
        for (Setting setting: settings) {
            switch (setting.getQuoteType()) {
                case CURRENCY_EXCHANGE:
                    currencyExchangeSet.add(setting.getQuoteSymbol());
                    break;
                case GOODS:
                    goodSet.add(setting.getQuoteSymbol());
                    break;
            }
        }
    }

    public String buildYahooFinanceMultiQueryUrl() {
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

        return baseYahooUrlreturnJsonPrepand + builder.toString() + baseYahooUrlreturnJsonAppend;
    }

    public String getYahooFinanceXchangeUrl() {
        return baseYahooUrlreturnJsonPrepand + getYahooFinanceXchangeQuery() + baseYahooUrlreturnJsonAppend;
    }

    public String getYahooFinanceQuotesUrl() {
        return baseYahooUrlreturnJsonPrepand + getYahooFinanceQuotesQuery() + baseYahooUrlreturnJsonAppend;
    }

    public String getYahooFinanceXchangeQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        for (String s: currencyExchangeSet) {
            builder.append(s);
            builder.append("'%2C'");
        }
        return yahooFinanceXchangeQueryUrl + builder.substring(0, builder.length() - 4) + ")";
    }

    public String getYahooFinanceQuotesQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        for (String s: goodSet) {
            builder.append(s);
            builder.append("'%2C'");
        }
        return yahooFinanceQuotesQueryUrl + builder.substring(0, builder.length() - 4) + ")";
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    // Convert the InputStream into a string
    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public String downloadQuotes() throws IOException {
        return downloadUrl(buildYahooFinanceMultiQueryUrl());
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
            Log.d(LOG_TAG, "The response is: " + response);
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
