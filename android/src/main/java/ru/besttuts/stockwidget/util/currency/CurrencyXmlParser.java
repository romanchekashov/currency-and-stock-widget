package ru.besttuts.stockwidget.util.currency;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.besttuts.stockwidget.io.HandleJSON;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;

/**
 * Создаем ресурсный файл с массивом валют.
 * http://developer.android.com/training/basics/network-ops/xml.html
 * Created by roman on 04.02.2015.
 */
public class CurrencyXmlParser {
    private static final String CURRENCY_LIST = "https://www.currency-iso.org/dam/downloads/lists/list_one.xml";
    private static final String YAHOO_CURRENCIES = "https://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote";

    private List<CcyNtry> yahooCurrencyCheck(List<CcyNtry> ccyNtries) {
        List<CcyNtry> newList = new ArrayList<>();
        List<YahooQuote> yahooQuotes = loadYahooQuotes();
        Set<String> yahooQuoteNames = new HashSet<>();

        for (YahooQuote quote: yahooQuotes) yahooQuoteNames.add(quote.name);

        for (CcyNtry ccyNtry: ccyNtries) {
            if(!yahooQuoteNames.contains(ccyNtry.Ccy)) continue;

            newList.add(ccyNtry);
        }

        return newList;
    }

    public void loadXmlAnrWriteToFile(String path) {

        String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources></resources>";
        try {
            data = loadXmlFromNetwork(CURRENCY_LIST);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.println(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (null != writer) writer.close();
    }

    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        List<CcyNtry> entries = null;

        try {
            stream = downloadUrl(urlString);
            entries = parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        if (entries.isEmpty()) return "";

        entries = yahooCurrencyCheck(entries);

        Collections.sort(entries, ALPHABETICAL_ORDER);

        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
        htmlString.append("<array name=\"currency_array\">\n");

        // StackOverflowXmlParser returns a List (called "entries") of CcyNtry objects.
        // Each CcyNtry object represents a single post in the XML feed.
        // This section processes the entries list to combine each entry with HTML markup.
        // Each entry is displayed in the UI as a CcyNm that optionally includes
        // a text Ccy.
        for (CcyNtry entry : entries) {
            htmlString.append(String.format("<item>%s(%s)</item>\n", entry.CcyNm, entry.Ccy));
        }

        htmlString.append("</array>\n</resources>");

        return htmlString.toString();
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    private List<CcyNtry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag();
            return CcyNtry.readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<YahooQuote> parseYahoo(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return YahooQuote.readFeed(parser);
        } finally {
            in.close();
        }
    }

    private static Comparator<CcyNtry> ALPHABETICAL_ORDER = new Comparator<CcyNtry>() {
        public int compare(CcyNtry str1, CcyNtry str2) {
            return String.CASE_INSENSITIVE_ORDER.compare(str1.CcyNm, str2.CcyNm);
        }
    };

    public List<YahooQuote> loadYahooQuotes(){
        InputStream stream = null;

        try {
            stream = downloadUrl(YAHOO_CURRENCIES);
            return parseYahoo(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new ArrayList<>();
    }
}
