package ru.besttuts.stockwidget.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by roman on 10.01.2015.
 */
public class QuoteContract {

    public interface SettingColumns {
        /** Unique string identifying this setting: SETTING_WIDGET_ID + "_" + "SETTING_QUOTE_POSITION". */
        String SETTING_ID = "setting_id";
        /** Title describing this block of time. */
        String SETTING_WIDGET_ID = "setting_widget_id";
        /** Time when this block starts. */
        String SETTING_QUOTE_POSITION = "setting_quote_position";
        /** Time when this block ends. */
        String SETTING_QUOTE_TYPE = "setting_quote_type";
        /** Type describing this block. */
        String SETTING_QUOTE_SYMBOL = "setting_quote_symbol";

        String LAST_TRADE_DATE = "last_trade_date";
    }

    public interface ModelColumns {
        /** Unique string identifying this setting: MODEL_WIDGET_ID + "_" + "MODEL_QUOTE_POSITION". */
        String MODEL_ID = "model_id";
        /** Time when this block starts. */
        String MODEL_NAME = "model_name";
        /** Time when this block ends. */
        String MODEL_RATE = "model_rate";
        /** Type describing this block. */
        String MODEL_CHANGE = "model_change";
        /** Extra subtitle for the block. */
        String MODEL_PERCENT_CHANGE = "model_percent_change";
        /** Extra subtitle for the block. */
        String MODEL_CURRENCY = "model_currency";
    }

    public interface QuoteColumns {
        String QUOTE_SYMBOL = "quote_symbol";
        String QUOTE_NAME = "quote_name";
        String QUOTE_TYPE = "quote_type";
    }

    public interface QuoteLastTradeDateColumns {
        String CODE = "code";
        String SYMBOL = "symbol";
        String LAST_TRADE_DATE = "last_trade_date";
    }

    public interface CurrencyExchangeColumns {
        /** Unique string identifying this block of time. */
        String CURRENCY_EXCHANGE_ID = "currency_exchange_id";
        /** Title describing this block of time. */
        String CURRENCY_EXCHANGE_SYMBOL = "currency_exchange_symbol";
        /** Time when this block starts. */
        String CURRENCY_EXCHANGE_RATE = "currency_exchange_rate";
        /** Time when this block ends. */
        String CURRENCY_EXCHANGE_CHANGE = "currency_exchange_change";
        /** Type describing this block. */
        String CURRENCY_EXCHANGE_PERCENT_CHANGE = "currency_exchange_percent_change";
        /** Extra subtitle for the block. */
        String CURRENCY_EXCHANGE_TIME = "currency_exchange_time";
    }

    // the symbolic name of the entire provider (its authority)
    public static final String CONTENT_AUTHORITY = "ru.besttuts.stockwidget";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_SETTINGS = "settings";

    public static class Settings implements SettingColumns, BaseColumns {
        public static final String BLOCK_TYPE_FREE = "free";
        public static final String BLOCK_TYPE_BREAK = "break";
        public static final String BLOCK_TYPE_KEYNOTE = "keynote";

        public static final boolean isValidBlockType(String type) {
            return BLOCK_TYPE_FREE.equals(type) ||  BLOCK_TYPE_BREAK.equals(type)
                    || BLOCK_TYPE_KEYNOTE.equals(type);
        }

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SETTINGS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.ru.besttuts.stockwidget.setting";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.ru.besttuts.stockwidget.setting";

        /** "ORDER BY" clauses. */
//        public static final String DEFAULT_SORT = BlocksColumns.BLOCK_START + " ASC, "
//                + BlocksColumns.BLOCK_END + " ASC";

        /** Build {@link Uri} for requested {@link #_ID}. */
        public static Uri buildUri(String entityId) {
            return CONTENT_URI.buildUpon().appendPath(entityId).build();
        }

        /** Read {@link #_ID} from {@link Settings} {@link Uri}. */
        public static String getId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

//        /**
//         * Generate a {@link #BLOCK_ID} that will always match the requested
//         * {@link Blocks} details.
//         * @param startTime the block start time, in milliseconds since Epoch UTC
//         * @param endTime the block end time, in milliseconds since Epoch UTF
//         */
//        public static String generateBlockId(long startTime, long endTime) {
//            startTime /= DateUtils.SECOND_IN_MILLIS;
//            endTime /= DateUtils.SECOND_IN_MILLIS;
//            return ParserUtils.sanitizeId(startTime + "-" + endTime);
//        }
    }
}
