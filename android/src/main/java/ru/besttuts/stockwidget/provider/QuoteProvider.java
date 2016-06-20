package ru.besttuts.stockwidget.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import ru.besttuts.stockwidget.BuildConfig;
import ru.besttuts.stockwidget.provider.QuoteContract.*;
import ru.besttuts.stockwidget.provider.QuoteContract.Settings;
import ru.besttuts.stockwidget.provider.QuoteContract.QuoteLastTradeDates;

import ru.besttuts.stockwidget.provider.QuoteDatabaseHelper.*;
import ru.besttuts.stockwidget.provider.QuoteDatabaseHelper.Tables;
import ru.besttuts.stockwidget.util.SelectionBuilder;

import java.util.Arrays;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class QuoteProvider extends ContentProvider {
    private static final String TAG = makeLogTag(QuoteProvider.class);

    private QuoteDatabaseHelper mDBOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int SETTINGS = 100;
    private static final int SETTINGS_ID = 101;

    private static final int MODELS = 200;
    private static final int MODELS_ID = 201;

    private static final int QUOTES = 300;
    private static final int QUOTES_ID = 301;

    private static final int QUOTE_LAST_TRADE_DATES = 400;
    private static final int QUOTE_LAST_TRADE_DATES_ID = 401;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = QuoteContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "settings", SETTINGS);
        matcher.addURI(authority, "settings/*", SETTINGS_ID);

        matcher.addURI(authority, "models", MODELS);
        matcher.addURI(authority, "models/*", MODELS_ID);

        matcher.addURI(authority, "models", QUOTES);
        matcher.addURI(authority, "models/*", QUOTES_ID);

        matcher.addURI(authority, "quote_last_trade_dates", QUOTE_LAST_TRADE_DATES);
        matcher.addURI(authority, "quote_last_trade_dates/*", QUOTE_LAST_TRADE_DATES_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDBOpenHelper = new QuoteDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mDBOpenHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);

        // avoid the expensive string concatenation below if not loggable
        if (BuildConfig.DEBUG) {
            LOGD(TAG, "uri=" + uri + " match=" + match + " proj=" + Arrays.toString(projection) +
                    " selection=" + selection + " args=" + Arrays.toString(selectionArgs) + ")");
        }

        switch (match) {
            default: {
                // Most cases are handled with simple SelectionBuilder
                final SelectionBuilder builder = buildExpandedSelection(uri, match);

                Cursor cursor = builder
                        .where(selection, selectionArgs)
                        .query(db, true, projection, sortOrder, null);
                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
        }
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)){
            case SETTINGS:
                return Settings.CONTENT_TYPE;
            case SETTINGS_ID:
                return Settings.CONTENT_ITEM_TYPE;
            case QUOTE_LAST_TRADE_DATES:
                return QuoteLastTradeDates.CONTENT_TYPE;
            case QUOTE_LAST_TRADE_DATES_ID:
                return QuoteLastTradeDates.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match){
            case SETTINGS:
                db.insertOrThrow(Tables.SETTINGS, null, values);
                return Settings.buildUri(values.getAsString(BaseColumns._ID));
            case QUOTE_LAST_TRADE_DATES:
                db.insertOrThrow(Tables.QUOTE_LAST_TRADE_DATES, null, values);
                return QuoteLastTradeDates.buildUri(values.getAsString(BaseColumns._ID));
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        LOGD(TAG, "delete(uri=" + uri + ")");

        final SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);

        return builder.where(selection, selectionArgs).delete(db);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        LOGD(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        final SelectionBuilder builder = buildSimpleSelection(uri);
        if (match == SETTINGS) {
            //TODO impl smth
        }
        return builder.where(selection, selectionArgs).update(db, values);
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #insert},
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SETTINGS:
                return builder.table(Tables.SETTINGS);
            case SETTINGS_ID:
                return builder.table(Tables.SETTINGS).where(Settings._ID + "=" + Settings.getId(uri));
            case QUOTE_LAST_TRADE_DATES:
                return builder.table(Tables.QUOTE_LAST_TRADE_DATES);
            case QUOTE_LAST_TRADE_DATES_ID:
                return builder.table(Tables.QUOTE_LAST_TRADE_DATES)
                        .where(QuoteLastTradeDates._ID + "=?", QuoteLastTradeDates.getId(uri));
            default:
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
        }
    }

    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case SETTINGS:
                return builder.table(Tables.SETTINGS);
            case SETTINGS_ID:
                return builder.table(Tables.SETTINGS).where(Settings._ID + "=?", Settings.getId(uri));
            case QUOTE_LAST_TRADE_DATES:
                return builder.table(Tables.QUOTE_LAST_TRADE_DATES);
            case QUOTE_LAST_TRADE_DATES_ID:
                return builder.table(Tables.QUOTE_LAST_TRADE_DATES)
                        .where(QuoteLastTradeDates._ID + "=?", QuoteLastTradeDates.getId(uri));
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
