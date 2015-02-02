package ru.besttuts.stockwidget.provider;

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

}
