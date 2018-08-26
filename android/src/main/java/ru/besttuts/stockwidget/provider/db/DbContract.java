package ru.besttuts.stockwidget.provider.db;

/**
 * Created by roman on 10.01.2015.
 */
@Deprecated
public interface DbContract {

    public interface SettingColumns {
        /** Unique string identifying this setting: SETTING_WIDGET_ID + "_" + "SETTING_QUOTE_POSITION". */
        String SETTING_ID = "setting_id";
        String SETTING_WIDGET_ID = "setting_widget_id";
        String SETTING_QUOTE_POSITION = "setting_quote_position";
        String SETTING_QUOTE_TYPE = "setting_quote_type";
        String SETTING_QUOTE_SYMBOL = "setting_quote_symbol";

        String LAST_TRADE_DATE = "last_trade_date";
    }

    public interface ModelColumns {
        /** Unique string identifying this setting: MODEL_WIDGET_ID + "_" + "MODEL_QUOTE_POSITION". */
        String MODEL_ID = "model_id";
        String MODEL_NAME = "model_name";
        String MODEL_RATE = "model_rate";
        String MODEL_CHANGE = "model_change";
        String MODEL_PERCENT_CHANGE = "model_percent_change";
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
        String CURRENCY_EXCHANGE_ID = "currency_exchange_id";
        String CURRENCY_EXCHANGE_SYMBOL = "currency_exchange_symbol";
        String CURRENCY_EXCHANGE_RATE = "currency_exchange_rate";
        String CURRENCY_EXCHANGE_CHANGE = "currency_exchange_change";
        String CURRENCY_EXCHANGE_PERCENT_CHANGE = "currency_exchange_percent_change";
        String CURRENCY_EXCHANGE_TIME = "currency_exchange_time";
    }
}
