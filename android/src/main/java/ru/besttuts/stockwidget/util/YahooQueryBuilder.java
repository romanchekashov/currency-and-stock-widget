package ru.besttuts.stockwidget.util;

import java.util.Set;

/**
 * @author rchekashov
 *         created on 06.10.2016
 */

public class YahooQueryBuilder {
    public static final String HTTP_QUERY_YAHOOAPIS_COM_V1_PUBLIC =
            "http://query.yahooapis.com/v1/public/";

    private static String yahooFinanceXchangeQuery = "select * from yahoo.finance.xchange where pair in (";
    private static String yahooFinanceQuotesQuery = "select * from yahoo.finance.quotes where symbol in (";

    public static String buildYahooFinanceMultiQuery(Set<String> currencyExchangeSet, Set<String> goodSet) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM query.multi WHERE queries=\"");
        if (null != currencyExchangeSet && currencyExchangeSet.size() > 0) {
            builder.append(yahooFinanceXchangeQuery +
                    transformCurrencyExchangeSetToString(currencyExchangeSet) + ")");
        }
        builder.append(";");
        if (null != goodSet && goodSet.size() > 0) {
            builder.append(yahooFinanceQuotesQuery + transformQuoteSetToString(goodSet) + ")");
        }
        builder.append("\"");

        return builder.toString();
    }

    private static String transformCurrencyExchangeSetToString(Set<String> currencyExchangeSet){
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        for (String s : currencyExchangeSet) {
            builder.append(s);
            builder.append("','");
        }
        return builder.substring(0, builder.length() - 2);
    }

    public static String transformQuoteSetToString(Set<String> goodSet){
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        for (String s : goodSet) {
            builder.append(s);
            builder.append("','");
        }
        return builder.substring(0, builder.length() - 2);
    }
}
