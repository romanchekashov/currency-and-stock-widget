package ru.besttuts.stockwidget.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.besttuts.stockwidget.io.ResultHandler;
import ru.besttuts.stockwidget.io.model.Result;
import ru.besttuts.stockwidget.sync.RemoteYahooFinanceDataFetcher;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

public class SymbolProvider extends ContentProvider {

    private static final String TAG = makeLogTag(SymbolProvider.class);

    public static String AUTHORITY = "ru.besttuts.stockwidget.provider.SymbolProvider";

    public static final Uri SEARCH_URI = Uri.parse("content://"+AUTHORITY+"/search");

    public static final Uri SUGGESTIONS_URI = Uri.parse("content://" + AUTHORITY + "/symbols");

    public static Map<String, Result> tempMap = new HashMap<>();

    private static final int SEARCH = 1;
    private static final int SUGGESTIONS = 2;

    // Defines a set of uris allowed with this content provider
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // URI for "Go" button
        uriMatcher.addURI(AUTHORITY, "search", SEARCH );

        // URI for suggestions in Search Dialog
        uriMatcher.addURI(AUTHORITY, "symbols",SUGGESTIONS);

        return uriMatcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        MatrixCursor mCursor = null;

        switch(mUriMatcher.match(uri)) {
            case SEARCH:
                // Defining a cursor object with columns id, SUGGEST_COLUMN_TEXT_1, SUGGEST_COLUMN_INTENT_EXTRA_DATA
                mCursor = new MatrixCursor(new String[] { "_id",
                        QuoteContract.QuoteColumns.QUOTE_NAME,
                        QuoteContract.QuoteColumns.QUOTE_SYMBOL }  );
                try {

                    String query = selectionArgs[0];
                    if (null == query || 2 > query.length()) {
                        return mCursor;
                    }

                    RemoteYahooFinanceDataFetcher fetcher = new RemoteYahooFinanceDataFetcher();

                    String data = fetcher.downloadUrl(
                            "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=" + query
                                    + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback");
                    String sJson = data.substring("YAHOO.Finance.SymbolSuggest.ssCallback(".length(),
                            data.length() - 1);

                    ResultHandler handler = new ResultHandler();
                    handler.readAndParseJSON(sJson);

                    List<Result> results = handler.getResults();
                    tempMap = new HashMap<>();
                    // Creating cursor object with places
                    for(int i = 0, ln = results.size(); i < ln; i++) {
                        Result result = results.get(i);

                        // Adding place details to cursor
                        mCursor.addRow(new String[] { Integer.toString(i), result.name, result.symbol });

                        tempMap.put(result.symbol, result); // найденный результаты помещаем во временное хранилище
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SUGGESTIONS:
                // Defining a cursor object with columns id, SUGGEST_COLUMN_TEXT_1, SUGGEST_COLUMN_INTENT_EXTRA_DATA
                mCursor = new MatrixCursor(new String[] { "_id",
                        SearchManager.SUGGEST_COLUMN_ICON_1,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID }  );
                Uri iconUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/drawable/ic_action_new");
                try {

                    String query = selectionArgs[0];
                    if (null == query || 2 > query.length()) {
                        return mCursor;
                    }

                    RemoteYahooFinanceDataFetcher fetcher = new RemoteYahooFinanceDataFetcher();

                    String data = fetcher.downloadUrl(
                            "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=" + query
                                    + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback");
                    String sJson = data.substring("YAHOO.Finance.SymbolSuggest.ssCallback(".length(),
                            data.length() - 1);

                    ResultHandler handler = new ResultHandler();
                    handler.readAndParseJSON(sJson);

                    List<Result> results = handler.getResults();
                    tempMap = new HashMap<>();
                    // Creating cursor object with places
                    for(int i = 0, ln = results.size(); i < ln; i++) {
                        Result result = results.get(i);

                        // Adding place details to cursor
                        mCursor.addRow(new String[] { Integer.toString(i), String.valueOf(iconUri),
                                result.name, result.name, result.symbol });

                        tempMap.put(result.symbol, result); // найденный результаты помещаем во временное хранилище
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }


        return mCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
