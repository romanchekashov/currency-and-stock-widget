package ru.besttuts.stockwidget.provider.loaders;


import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import java.util.List;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.provider.db.DbProvider;
import ru.besttuts.stockwidget.sync.money.MoneyRemoteService;
import ru.besttuts.stockwidget.sync.money.dto.QuoteDto;
import ru.besttuts.stockwidget.sync.money.dto.QuoteType;
import ru.besttuts.stockwidget.sync.money.dto.TickerSymbolsDto;
import ru.besttuts.stockwidget.ui.EconomicWidget;

public class MyCursorLoader extends CursorLoader {
    protected static String TAG = makeLogTag(MyCursorLoader.class);

    private static TickerSymbolsDto mTickerSymbolsDto;
    private static boolean isCommoditiesSaved;
    private static boolean isStockIndexSaved;
    private static boolean isBondSaved;
    private static boolean isCryptoSaved;

    QuoteDataSource mDataSource;
    private int mQuoteType;

    private Context mContext;

    public MyCursorLoader(Context context, QuoteDataSource dataSource,
                          int quoteType) {
        super(context);
        mContext = context;
        mDataSource = dataSource;
        mQuoteType = quoteType;
    }

    @Override
    public Cursor loadInBackground() {

        checkToFetchQuotes();
        checkQuotesToSave(mQuoteType);

        Cursor cursor = mDataSource.getQuoteCursor(mQuoteType);

        LOGD(TAG, String.format("loadInBackground: quoteType = %s, count = %d",
                mQuoteType, cursor.getCount()));

        return cursor;
    }

    private void checkToFetchQuotes() {
        if (mTickerSymbolsDto != null) return;

        try {
            MoneyRemoteService remoteService = new MoneyRemoteService(mContext);
            mTickerSymbolsDto = remoteService.tickerSymbols();

            LOGD(TAG, String.format("loadInBackground: quoteType = %s, mTickerSymbolsDto.getQuotes().size = %d",
                    mQuoteType, mTickerSymbolsDto.getQuotes().size()));
        } catch (Exception e) {
            LOGE(TAG, "" + e.getMessage());
            EconomicWidget.connectionStatus =
                    mContext.getString(R.string.connection_status_default_problem);
        }
    }

    private void checkQuotesToSave(int quoteType) {

        if (quoteType == QuoteType.COMMODITY.getNumVal() && !isCommoditiesSaved) {
            saveQuotes(quoteType);
            isCommoditiesSaved = true;
        }

        if (quoteType == QuoteType.STOCK_INDEX.getNumVal() && !isStockIndexSaved) {
            saveQuotes(quoteType);
            isStockIndexSaved = true;
        }

        if (quoteType == QuoteType.BOND.getNumVal() && !isBondSaved) {
            saveQuotes(quoteType);
            isBondSaved = true;
        }

        if (quoteType == QuoteType.CRYPTO.getNumVal() && !isCryptoSaved) {
            saveQuotes(quoteType);
            isCryptoSaved = true;
        }

    }

    private void saveQuotes(int quoteType) {
        List<QuoteDto> quotes = mTickerSymbolsDto.getQuotes();

        for (QuoteDto quote: quotes) {
            if (quoteType == quote.getType().getNumVal()) {
                DbProvider.getInstance().addQuote(quote.getSymbol(), quote.getName(), quote.getType().getNumVal());
            }
        }
    }
}
