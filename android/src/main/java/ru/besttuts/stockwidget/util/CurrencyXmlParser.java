package ru.besttuts.stockwidget.util;

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

    private List<CcyNtry> yahooCurrencyCheck(List<CcyNtry> ccyNtries) {
        List<CcyNtry> newList = new ArrayList<>(ccyNtries.size());

        RemoteYahooFinanceDataFetcher dataFetcher = new RemoteYahooFinanceDataFetcher();
        for (CcyNtry ccyNtry: ccyNtries) {
            dataFetcher.populateQuoteSet(QuoteType.CURRENCY, "USD"+ccyNtry.Ccy);
        }

        HandleJSON handleJSON = new HandleJSON(null);
        try {
            handleJSON.readAndParseJSON(dataFetcher.downloadQuotes());

            Map<String, Model> symbolModelMap = handleJSON.getSymbolModelMap();

            for (CcyNtry ccyNtry: ccyNtries) {
                String symbol = "USD" + ccyNtry.Ccy;
                Model model = symbolModelMap.get(symbol);

                if (null == model) continue;

                newList.add(ccyNtry);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return newList;
    }

    public void loadXmlAnrWriteToFile(String urlString, String path) {

        String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources></resources>";
        try {
            data = loadXmlFromNetwork(urlString);
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


    //************************************************************************************
    private static final String ns = null;

    // We don't use namespaces

    public List<CcyNtry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<CcyNtry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<CcyNtry> entries = new ArrayList<CcyNtry>();
        Set<String> uniqueSymbol = new HashSet<>();

        parser.require(XmlPullParser.START_TAG, ns, "CcyTbl");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("CcyNtry")) {
                CcyNtry ccyNtry = readEntry(parser);
                if (!uniqueSymbol.contains(ccyNtry.Ccy)) {
                    uniqueSymbol.add(ccyNtry.Ccy);
                    entries.add(ccyNtry);
                }
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // This class represents a single entry (post) in the XML feed.
    // It includes the data members "CtryNm," "CcyNm," and "Ccy."
    public static class CcyNtry {
        public final String CtryNm; // страна
        public final String CcyNm;  // название
        public final String Ccy;    // символ

        private CcyNtry(String CtryNm, String Ccy, String CcyNm) {
            this.CtryNm = CtryNm;
            this.Ccy = Ccy;
            this.CcyNm = CcyNm;
        }
    }

    private static Comparator<CcyNtry> ALPHABETICAL_ORDER = new Comparator<CcyNtry>() {
        public int compare(CcyNtry str1, CcyNtry str2) {
            return String.CASE_INSENSITIVE_ORDER.compare(str1.CcyNm, str2.CcyNm);
        }
    };

    // Parses the contents of an entry. If it encounters a CtryNm, Ccy, or CcyNm tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
    private CcyNtry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "CcyNtry");
        String CtryNm = null;
        String Ccy = null;
        String CcyNm = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("CtryNm")) {
                CtryNm = readCtryNm(parser);
            } else if (name.equals("Ccy")) {
                Ccy = readCcy(parser);
            } else if (name.equals("CcyNm")) {
                CcyNm = readCcyNm(parser);
            } else {
                skip(parser);
            }
        }
        return new CcyNtry(CtryNm, Ccy, CcyNm);
    }

    // Processes CtryNm tags in the feed.
    private String readCtryNm(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "CtryNm");
        String CtryNm = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "CtryNm");
        return CtryNm;
    }

    // Processes Ccy tags in the feed.
    private String readCcy(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Ccy");
        String Ccy = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Ccy");
        return Ccy;
    }

    // Processes CcyNm tags in the feed.
    private String readCcyNm(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "CcyNm");
        String CcyNm = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "CcyNm");
        return CcyNm;
    }

    // For the tags CtryNm and Ccy, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
