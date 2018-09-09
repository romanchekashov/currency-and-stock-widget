package ru.besttuts.stockwidget.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import ru.besttuts.stockwidget.provider.model.QuoteType;
import ru.besttuts.stockwidget.provider.QuoteContract.*;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * http://stackoverflow.com/questions/8133597/android-upgrading-db-version-and-adding-new-table
 *
 * db_version 3: insert some new oil prices
 * Created by roman on 10.01.2015.
 */
public class QuoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag(QuoteDatabaseHelper.class);

    private static final String DATABASE_NAME = "quote.db";
    private static final int DATABASE_VERSION = 5;

    private final Context mContext;

    public QuoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public interface Tables {
        String SETTINGS = "settings";
        String MODELS = "models";
        String QUOTES = "quotes";
        String QUOTE_LAST_TRADE_DATES = "quote_last_trade_dates";
        String CURRENCY_EXCHANGE = "currency_exchange";
        String STOCK = "stock";
        String GOODS = "goods";
        String INDICES = "indices";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.SETTINGS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SettingColumns.SETTING_ID + " TEXT NOT NULL,"
                + SettingColumns.SETTING_WIDGET_ID + " INTEGER NOT NULL,"
                + SettingColumns.SETTING_QUOTE_POSITION + " INTEGER NOT NULL,"
                + SettingColumns.SETTING_QUOTE_TYPE + " INTEGER NOT NULL,"
                + SettingColumns.SETTING_QUOTE_SYMBOL + " TEXT NOT NULL,"
                + SettingColumns.LAST_TRADE_DATE + " INTEGER,"
                + "UNIQUE (" + SettingColumns.SETTING_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.MODELS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ModelColumns.MODEL_ID + " TEXT NOT NULL,"
                + ModelColumns.MODEL_NAME + " TEXT NOT NULL,"
                + ModelColumns.MODEL_RATE + " NUMERIC(10,4) NOT NULL,"
                + ModelColumns.MODEL_CHANGE + " NUMERIC(10,4) NOT NULL,"
                + ModelColumns.MODEL_PERCENT_CHANGE + " TEXT NOT NULL,"
                + ModelColumns.MODEL_CURRENCY + " TEXT,"
                + "UNIQUE (" + ModelColumns.MODEL_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.QUOTES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + QuoteColumns.QUOTE_SYMBOL + " TEXT NOT NULL,"
                + QuoteColumns.QUOTE_NAME + " TEXT NOT NULL,"
                + QuoteColumns.QUOTE_TYPE + " INTEGER NOT NULL,"
                + "UNIQUE (" + QuoteColumns.QUOTE_SYMBOL + "))");

        db.execSQL("CREATE TABLE " + Tables.QUOTE_LAST_TRADE_DATES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + QuoteLastTradeDateColumns.SYMBOL + " TEXT NOT NULL,"
                + QuoteLastTradeDateColumns.CODE + " TEXT NOT NULL,"
                + QuoteLastTradeDateColumns.LAST_TRADE_DATE + " INTEGER,"
                + "UNIQUE (" + QuoteLastTradeDateColumns.SYMBOL + "))");

        createDefaults(db);
        createDefaultsQuoteLastTradeDate(db);

        LOGD(TAG, "onCreate: " + db);
    }

    private void createDefaults(SQLiteDatabase db) {
        insertQuote(db, "GCX15.CMX", "Gold", QuoteType.COMMODITY);
        insertQuote(db, "SIZ15.CMX", "Silver", QuoteType.COMMODITY);
        insertQuote(db, "PLZ15.NYM", "Platinum", QuoteType.COMMODITY);
        insertQuote(db, "PAZ15.NYM", "Palladium", QuoteType.COMMODITY);
        insertQuote(db, "HGX15.CMX", "Copper", QuoteType.COMMODITY);
        insertQuote(db, "BZF16.NYM", "Brent Oil", QuoteType.COMMODITY);
        insertQuote(db, "CLZ15.NYM", "Light Oil", QuoteType.COMMODITY);
        insertQuote(db, "NGZ15.NYM", "Natural Gas", QuoteType.COMMODITY);
        insertQuote(db, "CZ15.CBT", "Corn", QuoteType.COMMODITY);
        insertQuote(db, "SF16.CBT", "Soybeans", QuoteType.COMMODITY);
        insertQuote(db, "ZWZ15.CBT", "Wheat", QuoteType.COMMODITY);
        insertQuote(db, "CCH15.NYB", "Cocoa", QuoteType.COMMODITY);
        insertQuote(db, "KCH15.NYB", "Coffee", QuoteType.COMMODITY);
        insertQuote(db, "CTH15.NYB", "Cotton", QuoteType.COMMODITY);
        insertQuote(db, "LBH15.CME", "Lumber", QuoteType.COMMODITY);
        insertQuote(db, "OJH15.NYB", "Orange Juice", QuoteType.COMMODITY);
        insertQuote(db, "SBH15.NYB", "Sugar", QuoteType.COMMODITY);
        // indices
//        insertQuote(db, "MICEXINDEXCF.ME", "MICEX", QuoteType.INDICES);
        insertQuote(db, "RTS.RS", "RTS", QuoteType.INDICES);
//        insertQuote(db, "^DJI", "Dow Jones", QuoteType.INDICES);
        insertQuote(db, "^GSPC", "S&P 500", QuoteType.INDICES);
        insertQuote(db, "^IXIC", "Nasdaq Composite", QuoteType.INDICES);
        insertQuote(db, "^FTSE", "FTSE 100", QuoteType.INDICES);
        insertQuote(db, "^N225", "NIKKEY 225", QuoteType.INDICES);
        insertQuote(db, "^AEX", "AEX Index", QuoteType.INDICES);
        insertQuote(db, "^FCHI", "CAC 40", QuoteType.INDICES);
        insertQuote(db, "^BVLG", "PSI General", QuoteType.INDICES);
        // stock
        insertQuote(db, "AAPL", "Apple Inc.", QuoteType.STOCK);
        insertQuote(db, "GOOG", "Google Inc.", QuoteType.STOCK);
        insertQuote(db, "IBM", "IBM", QuoteType.STOCK);
        insertQuote(db, "MCD", "McDonald's", QuoteType.STOCK);
        insertQuote(db, "MSFT", "Microsoft", QuoteType.STOCK);
        insertQuote(db, "KO", "Coca-cola", QuoteType.STOCK);
        insertQuote(db, "T", "AT&T", QuoteType.STOCK);
        insertQuote(db, "PM", "Philip Morris International", QuoteType.STOCK);
        insertQuote(db, "CHL", "China Mobile Limited", QuoteType.STOCK);
        insertQuote(db, "GE", "GE", QuoteType.STOCK);
        insertQuote(db, "VOD", "Vodafone", QuoteType.STOCK);
        insertQuote(db, "VZ", "Verizon", QuoteType.STOCK);
        insertQuote(db, "AMZN", "Amazon", QuoteType.STOCK);
        insertQuote(db, "WMT", "Wal-Mart", QuoteType.STOCK);
        insertQuote(db, "WFC", "Wells Fargo", QuoteType.STOCK);
        insertQuote(db, "UPS", "UPS", QuoteType.STOCK);
        insertQuote(db, "HPQ", "Hewlett-Packard", QuoteType.STOCK);
        insertQuote(db, "TMUS", "T-Mobile", QuoteType.STOCK);
        insertQuote(db, "V", "Visa", QuoteType.STOCK);
        insertQuote(db, "ORCL", "Oracle", QuoteType.STOCK);
        insertQuote(db, "SAP", "SAP", QuoteType.STOCK);
        insertQuote(db, "CICHY", "China Construction Bank", QuoteType.STOCK);
        insertQuote(db, "BBRY", "BlackBerry", QuoteType.STOCK);
        insertQuote(db, "MC.PA", "Louis Vuitton", QuoteType.STOCK);
        insertQuote(db, "TM", "Toyota", QuoteType.STOCK);
        insertQuote(db, "HSBC", "HSBC", QuoteType.STOCK);
        insertQuote(db, "BIDU", "Baidu", QuoteType.STOCK);
        insertQuote(db, "BMW.F", "BMW", QuoteType.STOCK);
        insertQuote(db, "TESO", "Tesco", QuoteType.STOCK);
        insertQuote(db, "GILLETTE.BO", "Gillette", QuoteType.STOCK);
        insertQuote(db, "LFC", "China Life", QuoteType.STOCK);
        insertQuote(db, "FB", "Facebook", QuoteType.STOCK);
        insertQuote(db, "ORAN", "Orange", QuoteType.STOCK);
        insertQuote(db, "3988.HK", "Bank of China", QuoteType.STOCK);
        insertQuote(db, "DIS", "Disney", QuoteType.STOCK);
        insertQuote(db, "RY", "RBC(Royal Bank of Canada)", QuoteType.STOCK);
        insertQuote(db, "AXP", "American Express", QuoteType.STOCK);
        insertQuote(db, "XOM", "ExxonMobil", QuoteType.STOCK);
        insertQuote(db, "TD", "Toronto-Dominion Bank", QuoteType.STOCK);
        insertQuote(db, "CSCO", "Cisco", QuoteType.STOCK);
        insertQuote(db, "BUD", "Budweiser", QuoteType.STOCK);
        insertQuote(db, "OR.MI", "L'Oréal", QuoteType.STOCK);
        insertQuote(db, "C", "Citigroup Inc.", QuoteType.STOCK);
        insertQuote(db, "ACN", "Accenture", QuoteType.STOCK);
        insertQuote(db, "DAI.DE", "Daimler AG", QuoteType.STOCK);
        insertQuote(db, "SBER.ME", "SBERBANK", QuoteType.STOCK);
        insertQuote(db, "YNDX", "YANDEX", QuoteType.STOCK);

    }

    private void insertQuote(SQLiteDatabase db, String symbol,
                             String name, int quoteType) {
        ContentValues values = new ContentValues();
        values.put(QuoteContract.QuoteColumns.QUOTE_SYMBOL, symbol);
        values.put(QuoteContract.QuoteColumns.QUOTE_NAME, name);
        values.put(QuoteContract.QuoteColumns.QUOTE_TYPE, quoteType);

        db.insertWithOnConflict(Tables.QUOTES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void insertQuoteLastTradeDate(
            SQLiteDatabase db, String code, String symbol, long lastTradeDate) {
        ContentValues values = new ContentValues();
        values.put(QuoteLastTradeDateColumns.SYMBOL, symbol);
        values.put(QuoteLastTradeDateColumns.CODE, code);
        values.put(QuoteLastTradeDateColumns.LAST_TRADE_DATE, lastTradeDate);

        db.insertWithOnConflict(Tables.QUOTE_LAST_TRADE_DATES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void createDefaultsQuoteLastTradeDate(SQLiteDatabase db){
        insertQuoteLastTradeDate(db, "BZ", "BZZ15.NYM", 1447372800000L);
        insertQuoteLastTradeDate(db, "BZ", "BZJ15.NYM", 1426464000000L);
        insertQuoteLastTradeDate(db, "BZ", "BZF16.NYM", 1450224000000L);
        insertQuoteLastTradeDate(db, "BZ", "BZH16.NYM", 1454025600000L);
        insertQuoteLastTradeDate(db, "BZ", "BZJ16.NYM", 1456704000000L);
        insertQuoteLastTradeDate(db, "BZ", "BZK16.NYM", 1459382400000L);
        insertQuoteLastTradeDate(db, "BZ", "BZM16.NYM", 1461888000000L);
        insertQuoteLastTradeDate(db, "BZ", "BZN16.NYM", 1464652800000L);
        insertQuoteLastTradeDate(db, "BZ", "BZQ16.NYM", 1467244800000L);
        insertQuoteLastTradeDate(db, "BZ", "BZU16.NYM", 1469750400000L); // July

        insertQuoteLastTradeDate(db, "CL", "CLX15.NYM", 1445299200000L);
        insertQuoteLastTradeDate(db, "CL", "CLZ15.NYM", 1447977600000L);
        insertQuoteLastTradeDate(db, "CL", "CLH16.NYM", 1456099200000L);
        insertQuoteLastTradeDate(db, "CL", "CLJ16.NYM", 1458518400000L);
        insertQuoteLastTradeDate(db, "CL", "CLK16.NYM", 1461110400000L);
        insertQuoteLastTradeDate(db, "CL", "CLM16.NYM", 1463702400000L);
        insertQuoteLastTradeDate(db, "CL", "CLN16.NYM", 1466467200000L);
        insertQuoteLastTradeDate(db, "CL", "CLQ16.NYM", 1468972800000L); // July

        insertQuoteLastTradeDate(db, "GC", "GCV15.CMX", 1445990400000L);
        insertQuoteLastTradeDate(db, "GC", "GCX15.CMX", 1448409600000L);
        insertQuoteLastTradeDate(db, "GC", "GCF16.CMX", 1453852800000L);
        insertQuoteLastTradeDate(db, "GC", "GCG16.CMX", 1456358400000L);
        insertQuoteLastTradeDate(db, "GC", "GCH16.CMX", 1459209600000L);
        insertQuoteLastTradeDate(db, "GC", "GCJ16.CMX", 1461715200000L); // Apr

        insertQuoteLastTradeDate(db, "SI", "SIZ15.CMX", 1451347200000L);
        insertQuoteLastTradeDate(db, "SI", "SIF16.CMX", 1453852800000L);
        insertQuoteLastTradeDate(db, "SI", "SIG16.CMX", 1456358400000L);
        insertQuoteLastTradeDate(db, "SI", "SIH16.CMX", 1459209600000L); // Mar

        insertQuoteLastTradeDate(db, "PL", "PLF16.NYM", 1453852800000L);
        insertQuoteLastTradeDate(db, "PL", "PLG16.NYM", 1456358400000L);
        insertQuoteLastTradeDate(db, "PL", "PLH16.NYM", 1459209600000L); // Mar

        insertQuoteLastTradeDate(db, "PA", "PAF16.NYM", 1453852800000L);
        insertQuoteLastTradeDate(db, "PA", "PAG16.NYM", 1456358400000L);
        insertQuoteLastTradeDate(db, "PA", "PAH16.NYM", 1459209600000L); // Mar

        insertQuoteLastTradeDate(db, "HG", "HGF16.CMX", 1453852800000L);
        insertQuoteLastTradeDate(db, "HG", "HGG16.CMX", 1456358400000L);
        insertQuoteLastTradeDate(db, "HG", "HGH16.CMX", 1459209600000L); // Mar

        insertQuoteLastTradeDate(db, "NG", "NGG16.NYM", 1453852800000L);
        insertQuoteLastTradeDate(db, "NG", "NGH16.NYM", 1456358400000L);
        insertQuoteLastTradeDate(db, "NG", "NGJ16.NYM", 1459209600000L); // Mar

        insertQuoteLastTradeDate(db, "C", "CZ15.CBT", 1450051200000L);
        insertQuoteLastTradeDate(db, "C", "CH16.CBT", 1457913600000L);
        insertQuoteLastTradeDate(db, "C", "CK16.CBT", 1463097600000L); // May

        insertQuoteLastTradeDate(db, "S", "SF16.CBT", 1452729600000L);
        insertQuoteLastTradeDate(db, "S", "SH16.CBT", 1457913600000L);
        insertQuoteLastTradeDate(db, "S", "SK16.CBT", 1463097600000L); // May

        insertQuoteLastTradeDate(db, "ZW", "ZWZ15.CBT", 1450051200000L);
        insertQuoteLastTradeDate(db, "ZW", "ZWH16.CBT", 1457913600000L);
        insertQuoteLastTradeDate(db, "ZW", "ZWK16.CBT", 1463097600000L); // May

        insertQuoteLastTradeDate(db, "CC", "CCH15.NYB", 1426464000000L);
        insertQuoteLastTradeDate(db, "CC", "CCK16.NYB", 1463097600000L); // May

        insertQuoteLastTradeDate(db, "KC", "KCH15.NYB", 1426723200000L);
        insertQuoteLastTradeDate(db, "KC", "KCH16.NYB", 1458259200000L);
        insertQuoteLastTradeDate(db, "KC", "KCK16.NYB", 1463529600000L); // May

        insertQuoteLastTradeDate(db, "CT", "CTH15.NYB", 1425859200000L);
        insertQuoteLastTradeDate(db, "CT", "CTH16.NYB", 1457395200000L);
        insertQuoteLastTradeDate(db, "CT", "CTK16.NYB", 1462492800000L); // May

        insertQuoteLastTradeDate(db, "LB", "LBH15.CME", 1426204800000L);
        insertQuoteLastTradeDate(db, "LB", "LBH16.CME", 1458000000000L);
        insertQuoteLastTradeDate(db, "LB", "LBK16.CME", 1463097600000L); // May

        insertQuoteLastTradeDate(db, "OJ", "OJH15.NYB", 1426032000000L);
        insertQuoteLastTradeDate(db, "OJ", "OJH16.NYB", 1457568000000L);
        insertQuoteLastTradeDate(db, "OJ", "OJK16.NYB", 1462838400000L); // May

        insertQuoteLastTradeDate(db, "SB", "SBH15.NYB", 1424995200000L);
        insertQuoteLastTradeDate(db, "SB", "SBH16.NYB", 1456704000000L);
        insertQuoteLastTradeDate(db, "SB", "SBK16.NYB", 1461888000000L); // Apr
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        switch(oldVersion){
            case 1:
                db.execSQL("DROP TABLE IF EXISTS " + Tables.SETTINGS);
                db.execSQL("DROP TABLE IF EXISTS " + Tables.MODELS);
                db.execSQL("DROP TABLE IF EXISTS " + Tables.QUOTES);
                onCreate(db);
                break;
            case 2:
                insertQuote(db, "BZZ15.NYM", "Brent(Dec) Oil", QuoteType.COMMODITY);
                insertQuote(db, "CLX15.NYM", "Light(Nov) Oil", QuoteType.COMMODITY);
                insertQuote(db, "CLZ15.NYM", "Light(Dec) Oil", QuoteType.COMMODITY);
                insertQuote(db, "GCV15.CMX", "Gold(Oct)", QuoteType.COMMODITY);
                insertQuote(db, "SIV15.CMX", "Silver(Oct)", QuoteType.COMMODITY);
            case 3:
                insertQuote(db, "GCX15.CMX", "Gold(Current)", QuoteType.COMMODITY);
                insertQuote(db, "SIZ15.CMX", "Silver(Current)", QuoteType.COMMODITY);
                insertQuote(db, "PLZ15.NYM", "Platinum(Current)", QuoteType.COMMODITY);
                insertQuote(db, "PAZ15.NYM", "Palladium(Current)", QuoteType.COMMODITY);
                insertQuote(db, "HGX15.CMX", "Copper(Current)", QuoteType.COMMODITY);
                insertQuote(db, "BZF16.NYM", "Brent(Current) Oil", QuoteType.COMMODITY);
                insertQuote(db, "NGZ15.NYM", "Natural Gas(Current)", QuoteType.COMMODITY);
                insertQuote(db, "CZ15.CBT", "Corn(Current)", QuoteType.COMMODITY);
                insertQuote(db, "SF16.CBT", "Soybeans(Current)", QuoteType.COMMODITY);
                insertQuote(db, "ZWZ15.CBT", "Wheat(Current)", QuoteType.COMMODITY);
            case 4:
                db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER",
                        Tables.SETTINGS, SettingColumns.LAST_TRADE_DATE));

                db.execSQL("CREATE TABLE " + Tables.QUOTE_LAST_TRADE_DATES + " ("
                        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + QuoteLastTradeDateColumns.CODE + " TEXT NOT NULL,"
                        + QuoteLastTradeDateColumns.SYMBOL + " TEXT NOT NULL,"
                        + QuoteLastTradeDateColumns.LAST_TRADE_DATE + " INTEGER,"
                        + "UNIQUE (" + QuoteLastTradeDateColumns.SYMBOL + "))");

                createDefaultsQuoteLastTradeDate(db);
            default:
                break;
        }

    }

}
