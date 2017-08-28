package ru.besttuts.stockwidget.util.currency;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author rchekashov
 *         created on 28.08.2017
 */

public class YahooQuote {
    final String name;

    public YahooQuote(String name) {
        this.name = name;
    }

    private static final String ns = null;

    static List<YahooQuote> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "list");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("resources")) {
                return readResources(parser);
            } else {
                skip(parser);
            }
        }
        return new ArrayList<>();
    }

    private static List<YahooQuote> readResources(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<YahooQuote> entries = new ArrayList<>();
        Set<String> uniqueSymbol = new HashSet<>();

        parser.require(XmlPullParser.START_TAG, ns, "resources");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("resource")) {
                YahooQuote quote = readEntry(parser);
                if (!uniqueSymbol.contains(quote.name)) {
                    uniqueSymbol.add(quote.name);
                    entries.add(quote);
                }
            } else {
                skip(parser);
            }
        }

        return entries;
    }

    static YahooQuote readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "resource");
        String name = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if("name".equalsIgnoreCase(parser.getAttributeValue(0))){
                name = readText(parser);
            } else {
                skip(parser);
            }
        }
        return new YahooQuote(name.replace("USD/", ""));
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
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
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
