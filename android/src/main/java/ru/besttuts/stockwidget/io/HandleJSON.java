package ru.besttuts.stockwidget.io;

import android.util.Log;

import ru.besttuts.stockwidget.model.Currency;
import ru.besttuts.stockwidget.model.Good;
import ru.besttuts.stockwidget.model.Model;

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

/**
 * Created by roman on 05.01.2015.
 */
public class HandleJSON {

    final String LOG_TAG = "EconomicWidget.HandleJSON";

    private List<Currency> currencies = null;
    private String sUrl = null;

    private Map<String, Model> symbolModelMap = new HashMap<>();

    public HandleJSON() { }

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

    public void readAndParseJSON(String in) {
        try {
            JSONObject reader = new JSONObject(in);

            JSONArray results = reader.getJSONObject("query").getJSONObject("results").getJSONArray("results");

            Object json = results.getJSONObject(0).get("rate");
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

            json = results.getJSONObject(1).get("quote");
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

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private List readCurrencyArray(JSONArray rate) throws JSONException, IOException {
        List<Currency> currencies = new ArrayList();

        for (int i = 0, ln = rate.length(); i < ln; i++) {
            JSONObject o = rate.optJSONObject(i);

            Currency currency = readCurrency(o);

            currencies.add(currency);

            Log.d(LOG_TAG, "readCurrencyArray: "+ currency.toString());
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
        Double ask = Double.parseDouble(o.getString("Ask"));
        Double bid = Double.parseDouble(o.getString("Bid"));
        currency.setChange(ask - bid);

        symbolModelMap.put(o.getString("id"), currency);

        return currency;
    }

    public List getCurrencies() {
        return currencies;
    }

    public void run() {

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
            readAndParseJSON(data);

            in.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static Map<String, String> QUOTE_SYMBOL_RUS = new HashMap<>();

    static {
        QUOTE_SYMBOL_RUS.put("HGF15.CMX", "Медь");
        QUOTE_SYMBOL_RUS.put("GCF15.CMX", "Золото");
        QUOTE_SYMBOL_RUS.put("PAF15.NYM", "Палладий");
        QUOTE_SYMBOL_RUS.put("PLF15.NYM", "Платина");
        QUOTE_SYMBOL_RUS.put("SIF15.CMX", "Серебро");
    }

    private List readGoodArray(JSONArray rate) throws JSONException, IOException {
        List<Good> goods = new ArrayList();

        for (int i = 0, ln = rate.length(); i < ln; i++) {
            JSONObject o = rate.optJSONObject(i);

            Good good = readGood(o);

            goods.add(good);

            Log.d(LOG_TAG, "readGoodArray: "+ good.toString());
        }

        return goods;
    }

    private Good readGood(JSONObject o) throws JSONException, IOException {
        Good good = new Good();
        good.setSymbol(o.getString("symbol"));
        good.setRate(o.getDouble("LastTradePriceOnly"));
        good.setChange(o.getDouble("Change"));
        good.setPercentChange(o.getString("ChangeinPercent"));
        good.setName(QUOTE_SYMBOL_RUS.get(o.getString("symbol")));

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
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
