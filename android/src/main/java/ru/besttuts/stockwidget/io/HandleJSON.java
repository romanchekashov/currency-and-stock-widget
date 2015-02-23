package ru.besttuts.stockwidget.io;

import android.content.Context;
import android.util.Log;

import ru.besttuts.stockwidget.model.Currency;
import ru.besttuts.stockwidget.model.Good;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 05.01.2015.
 */
public class HandleJSON {

    private static final String TAG = makeLogTag(HandleJSON.class);

    private List<Currency> currencies = null;
    private String sUrl = null;

    private Map<String, Model> symbolModelMap = new HashMap<>();

    private Context mContext;

    public HandleJSON(Context context) {
        mContext = context;
    }

    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public Map<String, Model> getSymbolModelMap() {
        return symbolModelMap;
    }

    public List readAndParseCurrencyJSON(String in) {
        JSONObject reader = null;
        try {
            reader = new JSONObject(in);

            JSONArray rate  = reader.getJSONObject("query").getJSONObject("results").getJSONArray("rate");

            return readCurrencyArray(rate);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList();
    }

    public List readAndParseGoodsJSON(String in) {
        JSONObject reader = null;
        try {
            reader = new JSONObject(in);

            JSONArray rate  = reader.getJSONObject("query").getJSONObject("results").getJSONArray("quote");

            return readGoodArray(rate);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList();
    }

    private void readResultsJSONObject(JSONObject jsonObject) throws IOException, JSONException {
        if (!jsonObject.isNull("rate")) {
            Object json = jsonObject.get("rate");
            if (null != json) {
                if (json instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) json;
                    if (jsonArray.length() > 0) {
                        readCurrencyArray(jsonArray);
                    }
                } else {
                    readCurrency((JSONObject) json);
                }
            }
        } else if (!jsonObject.isNull("quote")) {
            Object json = jsonObject.get("quote");
            if (null != json) {
                if (json instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) json;
                    if (jsonArray.length() > 0) {
                        readGoodArray(jsonArray);
                    }
                } else {
                    readGood((JSONObject) json);
                }
            }
        }
    }

    public void readAndParseJSON(String in) {
        try {

            LOGD(TAG, "readAndParseJSON: " + in);

            JSONObject reader = new JSONObject(in);

            if (reader.isNull("query") || reader.getJSONObject("query").isNull("results")) {
                return;
            }

            Object oResults = reader.getJSONObject("query").getJSONObject("results").get("results");

            if (oResults instanceof JSONArray) {
                JSONArray results = (JSONArray) oResults;

                for (int i = 0; i < results.length(); i++) {
                    readResultsJSONObject(results.getJSONObject(i));
                }

            } else if(oResults instanceof JSONObject) {
                readResultsJSONObject((JSONObject) oResults);
            }

        } catch (JSONException jsone) {
            LOGE(TAG, jsone.getMessage());
            jsone.printStackTrace();
        } catch (IOException ioe) {
            LOGE(TAG, ioe.getMessage());
            ioe.printStackTrace();
        }

    }

    private List readCurrencyArray(JSONArray rate) throws JSONException, IOException {
        List<Currency> currencies = new ArrayList();

        for (int i = 0, ln = rate.length(); i < ln; i++) {
            JSONObject o = rate.optJSONObject(i);

            Currency currency = readCurrency(o);

            currencies.add(currency);

            LOGD(TAG, "readCurrencyArray: " + currency.toString());
        }

        return currencies;
    }

    private Currency readCurrency(JSONObject o) throws JSONException, IOException {
        Currency currency = new Currency();
        currency.setId(o.getString("id"));
        if(null != o.getString("Name")) {
            currency.setName(o.getString("Name").replace(" to ", "/"));
        }
        currency.setRate(o.getDouble("Rate"));

        try {
            Double ask = Double.parseDouble(o.getString("Ask"));
            Double bid = Double.parseDouble(o.getString("Bid"));
            currency.setChange(ask - bid);

            symbolModelMap.put(o.getString("id"), currency);

        } catch (NumberFormatException e) {
            LOGE(TAG, e.getMessage());
        }

        return currency;
    }

    public List getCurrencies() {
        return currencies;
    }

    public void run() {

        InputStream in = null;
        try {
            URL url = new URL(sUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();

            in = conn.getInputStream();

            String data = HandleJSON.convertStreamToString(in);
            readAndParseJSON(data);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            LOGE(TAG, e.getMessage());
        } catch (ProtocolException e) {
            LOGE(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGE(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (null != in) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private List readGoodArray(JSONArray rate) throws JSONException, IOException {
        List<Good> goods = new ArrayList();

        for (int i = 0, ln = rate.length(); i < ln; i++) {
            JSONObject o = rate.optJSONObject(i);

            Good good = readGood(o);

            goods.add(good);

            LOGD(TAG, "readGoodArray: " + good.toString());
        }

        return goods;
    }

    private Good readGood(JSONObject o) throws JSONException, IOException {
        Good good = new Good();
        good.setId(o.getString("symbol"));
        good.setSymbol(o.getString("symbol"));
        try {
            good.setRate(o.getDouble("LastTradePriceOnly"));
        } catch (JSONException e) {
            LOGE(TAG, e.getMessage());
            good.setRate(0.0);
        }
        try {
            good.setChange(o.getDouble("Change"));
        } catch (JSONException e) {
            LOGE(TAG, e.getMessage());
            good.setChange(0.0);
        }
        good.setPercentChange(o.getString("ChangeinPercent"));
        good.setName(o.getString("Name"));
        good.setCurrency(o.getString("Currency"));
        
        symbolModelMap.put(o.getString("symbol"), good);

        return good;
    }

    public List<Good> getGoods(String sUrl) {

        try {
            URL url = new URL(sUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();

            InputStream in = conn.getInputStream();

            String data = HandleJSON.convertStreamToString(in);

            try {
                JSONObject reader = new JSONObject(data);

                JSONArray rate  = reader.getJSONObject("query").getJSONObject("results").getJSONArray("quote");

                return readGoodArray(rate);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (null != in) in.close();
            }

        } catch (MalformedURLException e) {
            LOGE(TAG, e.getMessage());
            e.printStackTrace();
        } catch (ProtocolException e) {
            LOGE(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGE(TAG, e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
