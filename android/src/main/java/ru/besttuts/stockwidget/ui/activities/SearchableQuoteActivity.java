/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.besttuts.stockwidget.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.MenuItemCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.io.model.Result;
import ru.besttuts.stockwidget.provider.QuoteContract;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.provider.SymbolProvider;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * The main activity for the dictionary.
 * Displays search results triggered by the search dialog and handles
 * actions from search suggestions.
 */
@Deprecated
public class SearchableQuoteActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = makeLogTag(SearchableQuoteActivity.class);

    private int mAppWidgetId;
    private int mQuoteTypeValue;

    private QuoteDataSource mDataSource;

    private SimpleCursorAdapter mSimpleCursorAdapter;

    // Идентификатор загрузчика используемый в данном компоненте
    private static final int URL_LOADER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) { // TODO Closing after rotate!
        super.onCreate(savedInstanceState);

//        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
//            finish();
//            return;
//        }

        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

//        Utils.onActivityCreateSetActionBarColor(getSupportActionBar());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(EconomicWidgetConfigureActivity.getColor(this, false));
        }
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(EconomicWidgetConfigureActivity.getColor(this, true)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (null == savedInstanceState) {
            Bundle b = getIntent().getExtras();
            mAppWidgetId = b.getInt(EconomicWidgetConfigureActivity.ARG_WIDGET_ID);
            mQuoteTypeValue = b.getInt(EconomicWidgetConfigureActivity.ARG_QUOTE_TYPE_VALUE);
        } else {
            mAppWidgetId = savedInstanceState.getInt(
                    EconomicWidgetConfigureActivity.ARG_WIDGET_ID);
            mQuoteTypeValue = savedInstanceState.getInt(
                    EconomicWidgetConfigureActivity.ARG_QUOTE_TYPE_VALUE);
        }

        String[] from = new String[]{QuoteContract.QuoteColumns.QUOTE_NAME,
                QuoteContract.QuoteColumns.QUOTE_SYMBOL};

        int[] to = new int[]{android.R.id.text1, android.R.id.text2};

        mSimpleCursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, null, from, to, 0);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(mSimpleCursorAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LOGD(TAG, "onItemClick: " + mSimpleCursorAdapter.getItem(position));
                Cursor cursor = mSimpleCursorAdapter.getCursor();
                cursor.moveToPosition(position);
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow(
                        QuoteContract.QuoteColumns.QUOTE_SYMBOL));
                Result result = SymbolProvider.tempMap.get(symbol);
                if (null != mDataSource) {
                    try {
                        mDataSource.addQuoteRec(result);
                        finish();
                    } catch (IllegalArgumentException e) {
                        LOGE(TAG, e.getMessage());
                        Toast.makeText(SearchableQuoteActivity.this,
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mDataSource = new QuoteDataSource(this);

        // создаем лоадер для чтения данных
        Loader loader = getSupportLoaderManager().getLoader(URL_LOADER);
        if (null == loader) {
            LOGD(TAG, "Loader is null");
            getSupportLoaderManager().initLoader(URL_LOADER, null, this);
        } else {
            LOGD(TAG, "Loader is " + loader);
            loader.forceLoad();
        }

        LOGD(TAG, "onCreate: intent: " + getIntent());

//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutSearch);
//        linearLayout.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                LOGD(TAG, "onKey: searchView: " + searchView + ", mMenu: " + mMenu);
//
//                return false;
//            }
//        });

        handleIntent(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EconomicWidgetConfigureActivity.ARG_WIDGET_ID, mAppWidgetId);
        outState.putInt(EconomicWidgetConfigureActivity.ARG_QUOTE_TYPE_VALUE, mQuoteTypeValue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LOGD(TAG, "onResume: currentThread = " + Thread.currentThread());
        Loader loader = getSupportLoaderManager().getLoader(URL_LOADER);
        if (null != loader) {
            LOGD(TAG, "Loader is " + loader);
            loader.forceLoad();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mDataSource) mDataSource.close();
        getSupportLoaderManager().destroyLoader(URL_LOADER);
        LOGD(TAG, "onDestroy");
    }

    private SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activity_actions, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if (null == searchView) {
            LOGE(TAG, "searchView is " + searchView);
            return super.onCreateOptionsMenu(menu);
        }

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LOGD(TAG, "onKeyUp: searchView: " + searchView);
            if (null != searchView) {
                searchView.setQuery("", false);
                searchView.clearFocus();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                onSearchRequested();
                return true;
            case android.R.id.home: // Respond to the action bar's Up/Home button
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(EconomicWidgetConfigureActivity.ARG_WIDGET_ID, mAppWidgetId);
                upIntent.putExtra(EconomicWidgetConfigureActivity.ARG_QUOTE_TYPE_VALUE, mQuoteTypeValue);

                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Bundle data = new Bundle();
            data.putString("query", intent.getStringExtra(SearchManager.QUERY));
            getSupportLoaderManager().restartLoader(URL_LOADER, data, this);
        }
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            Uri data = intent.getData();
            Result result = SymbolProvider.tempMap.get(
                    data.getPath().substring("/symbols/".length()));
            if (null != mDataSource) {
                mDataSource.addQuoteRec(result);
            }
            LOGD(TAG, "handleIntent(Intent.ACTION_VIEW): " + data);

            searchView.setQuery("", false);
            searchView.clearFocus();
            // intent.getExtras().getString(SearchManager.EXTRA_DATA_KEY)
//            showResult(data);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = "";
        if (null != args) {
            query = args.getString("query");
        }
        return new CursorLoader(this, SymbolProvider.SEARCH_URI, null, null,
                new String[]{query}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSimpleCursorAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSimpleCursorAdapter.changeCursor(null);
    }

}
