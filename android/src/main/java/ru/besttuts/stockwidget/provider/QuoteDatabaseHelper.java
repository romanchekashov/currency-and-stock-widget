package ru.besttuts.stockwidget.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import ru.besttuts.stockwidget.model.QuoteType;
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
    private static final int DATABASE_VERSION = 4;

    private final Context mContext;

    public QuoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public interface Tables {
        String SETTINGS = "settings";
        String MODELS = "models";
        String QUOTES = "quotes";
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

        createDefaults(db);

        LOGD(TAG, "onCreate: " + db);
    }

    private void createDefaults(SQLiteDatabase db) {
        insertQuote(db, "GCX15.CMX", "Gold", QuoteType.GOODS);
        insertQuote(db, "SIZ15.CMX", "Silver", QuoteType.GOODS);
        insertQuote(db, "PLZ15.NYM", "Platinum", QuoteType.GOODS);
        insertQuote(db, "PAZ15.NYM", "Palladium", QuoteType.GOODS);
        insertQuote(db, "HGX15.CMX", "Copper", QuoteType.GOODS);
        insertQuote(db, "BZF16.NYM", "Brent Oil", QuoteType.GOODS);
        insertQuote(db, "CLZ15.NYM", "Light Oil", QuoteType.GOODS);
        insertQuote(db, "NGZ15.NYM", "Natural Gas", QuoteType.GOODS);
        insertQuote(db, "CZ15.CBT", "Corn", QuoteType.GOODS);
        insertQuote(db, "SF16.CBT", "Soybeans", QuoteType.GOODS);
        insertQuote(db, "ZWZ15.CBT", "Wheat", QuoteType.GOODS);
        insertQuote(db, "CCH15.NYB", "Cocoa", QuoteType.GOODS);
        insertQuote(db, "KCH15.NYB", "Coffee", QuoteType.GOODS);
        insertQuote(db, "CTH15.NYB", "Cotton", QuoteType.GOODS);
        insertQuote(db, "LBH15.CME", "Lumber", QuoteType.GOODS);
        insertQuote(db, "OJH15.NYB", "Orange Juice", QuoteType.GOODS);
        insertQuote(db, "SBH15.NYB", "Sugar", QuoteType.GOODS);
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
                insertQuote(db, "BZZ15.NYM", "Brent(Dec) Oil", QuoteType.GOODS);
                insertQuote(db, "CLX15.NYM", "Light(Nov) Oil", QuoteType.GOODS);
                insertQuote(db, "CLZ15.NYM", "Light(Dec) Oil", QuoteType.GOODS);
                insertQuote(db, "GCV15.CMX", "Gold(Oct)", QuoteType.GOODS);
                insertQuote(db, "SIV15.CMX", "Silver(Oct)", QuoteType.GOODS);
            case 3:
                insertQuote(db, "GCX15.CMX", "Gold(Current)", QuoteType.GOODS);
                insertQuote(db, "SIZ15.CMX", "Silver(Current)", QuoteType.GOODS);
                insertQuote(db, "PLZ15.NYM", "Platinum(Current)", QuoteType.GOODS);
                insertQuote(db, "PAZ15.NYM", "Palladium(Current)", QuoteType.GOODS);
                insertQuote(db, "HGX15.CMX", "Copper(Current)", QuoteType.GOODS);
                insertQuote(db, "BZF16.NYM", "Brent(Current) Oil", QuoteType.GOODS);
                insertQuote(db, "NGZ15.NYM", "Natural Gas(Current)", QuoteType.GOODS);
                insertQuote(db, "CZ15.CBT", "Corn(Current)", QuoteType.GOODS);
                insertQuote(db, "SF16.CBT", "Soybeans(Current)", QuoteType.GOODS);
                insertQuote(db, "ZWZ15.CBT", "Wheat(Current)", QuoteType.GOODS);
            default:
                break;
        }

    }

}
