package ru.besttuts.stockwidget.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.provider.QuoteContract.*;

/**
 * Created by roman on 10.01.2015.
 */
public class QuoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "EconomicWidget.QuoteDatabase";

    private static final String DATABASE_NAME = "quote.db";
    private static final int DATABASE_VERSION = 2;

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
                + SettingColumns.SETTING_QUOTE_TYPE + " TEXT NOT NULL,"
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
                + QuoteColumns.QUOTE_TYPE + " TEXT NOT NULL,"
                + "UNIQUE (" + QuoteColumns.QUOTE_SYMBOL + "))");

        createDefaults(db);
    }

    private void createDefaults(SQLiteDatabase db) {
        insertQuote(db, "GCF15.CMX", "Gold", QuoteType.GOODS);
        insertQuote(db, "SIF15.CMX", "Silver", QuoteType.GOODS);
        insertQuote(db, "PLF15.NYM", "Platinum", QuoteType.GOODS);
        insertQuote(db, "PAF15.NYM", "Palladium", QuoteType.GOODS);
        insertQuote(db, "HGF15.CMX", "Copper", QuoteType.GOODS);
        insertQuote(db, "BZH15.NYM", "Brent Oil", QuoteType.GOODS);
        insertQuote(db, "NGH15.NYM", "Natural Gas", QuoteType.GOODS);
        insertQuote(db, "CH15.CBT", "Corn", QuoteType.GOODS);
        insertQuote(db, "SH15.CBT", "Soybeans", QuoteType.GOODS);
        insertQuote(db, "ZWH15.CBT", "Wheat", QuoteType.GOODS);

        insertQuote(db, "CCH15.NYB", "Cocoa", QuoteType.GOODS);
        insertQuote(db, "KCH15.NYB", "Coffee", QuoteType.GOODS);
        insertQuote(db, "CTH15.NYB", "Cotton", QuoteType.GOODS);
        insertQuote(db, "LBH15.CME", "Lumber", QuoteType.GOODS);
        insertQuote(db, "OJH15.NYB", "Orange Juice", QuoteType.GOODS);
        insertQuote(db, "SBH15.NYB", "Sugar", QuoteType.GOODS);
    }

    private void insertQuote(SQLiteDatabase db, String symbol, String name, QuoteType quoteType) {
        ContentValues values = new ContentValues();
        values.put(QuoteContract.QuoteColumns.QUOTE_SYMBOL, symbol);
        values.put(QuoteContract.QuoteColumns.QUOTE_NAME, name);
        values.put(QuoteContract.QuoteColumns.QUOTE_TYPE, String.valueOf(quoteType));

        db.insertWithOnConflict(Tables.QUOTES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.MODELS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.QUOTES);
        onCreate(db);
    }

}
