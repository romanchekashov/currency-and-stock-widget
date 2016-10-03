package ru.besttuts.stockwidget.sync.model;

import java.util.Date;
import java.util.List;

/**
 * @author rchekashov
 *         created on 04.10.2016
 */

public class YahooMultiQueryData {
    public final Query query;

    public YahooMultiQueryData(Query query) {
        this.query = query;
    }

    public static class Query {
        public final int count;
        public final Date created;
        public final String lang;
        public final ResultsWrap results;

        public Query(int count, Date created, String lang, ResultsWrap results) {
            this.count = count;
            this.created = created;
            this.lang = lang;
            this.results = results;
        }
    }

    public static class ResultsWrap {
        public final List<Object> results;

        public ResultsWrap(List<Object> results) {
            this.results = results;
        }
    }
}
