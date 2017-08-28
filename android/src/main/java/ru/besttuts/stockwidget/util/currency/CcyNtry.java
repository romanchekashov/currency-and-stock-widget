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
 *
 * This class represents a single entry (post) in the XML feed.
 * It includes the data members "CtryNm," "CcyNm," and "Ccy."
 */
public class CcyNtry {
    public final String CtryNm; // страна
    public final String CcyNm;  // название
    public final String Ccy;    // символ

    public CcyNtry(String CtryNm, String Ccy, String CcyNm) {
        this.CtryNm = CtryNm;
        this.Ccy = Ccy;
        this.CcyNm = CcyNm;
    }

    private static final String ns = null;

    static List<CcyNtry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
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

    // Parses the contents of an entry. If it encounters a CtryNm, Ccy, or CcyNm tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
    static CcyNtry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
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
    private static String readCtryNm(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "CtryNm");
        String CtryNm = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "CtryNm");
        return CtryNm;
    }

    // Processes Ccy tags in the feed.
    private static String readCcy(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Ccy");
        String Ccy = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Ccy");
        return Ccy;
    }

    // Processes CcyNm tags in the feed.
    private static String readCcyNm(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "CcyNm");
        String CcyNm = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "CcyNm");
        return CcyNm;
    }

    // For the tags CtryNm and Ccy, extracts their text values.
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
