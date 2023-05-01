package ru.besttuts.stockwidget.provider.loaders;


import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGE;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Quote;
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

    private int mQuoteType;

    private Context mContext;

    public MyCursorLoader(Context context, int quoteType) {
        super(context);
        mContext = context;
        mQuoteType = quoteType;
    }

    @Override
    public Cursor loadInBackground() {
        checkToFetchQuotes();
        checkQuotesToSave(mQuoteType);

        Cursor cursor = DbProvider.getInstance().db().getQuoteCursor(mQuoteType);

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
            deleteAndSaveQuotes(quoteType);
            isCommoditiesSaved = true;
        }

        if (quoteType == QuoteType.STOCK_INDEX.getNumVal() && !isStockIndexSaved) {
            deleteAndSaveQuotes(quoteType);
            isStockIndexSaved = true;
        }

        if (quoteType == QuoteType.BOND.getNumVal() && !isBondSaved) {
            deleteAndSaveQuotes(quoteType);
            isBondSaved = true;
        }

        if (quoteType == QuoteType.CRYPTO.getNumVal() && !isCryptoSaved) {
            deleteAndSaveQuotes(quoteType);
            isCryptoSaved = true;
        }

    }

    private void deleteAndSaveQuotes(int quoteType) {
        List<QuoteDto> quotes = filterQuotes(quoteType);

        deleteNonExistingQuotes(quotes);

        for (QuoteDto quote: quotes) {
            DbProvider.getInstance().addQuote(quote.getSymbol(), quote.getName(), quote.getType().getNumVal());
        }
    }

    private void deleteNonExistingQuotes(List<QuoteDto> quotes) {
        int quoteType = quotes.get(0).getType().getNumVal();
        List<Quote> models = DbProvider.getInstance().getQuotes(quoteType);

        Set<String> existingSymbols = new HashSet<>();
        for (Quote q: models) existingSymbols.add(q.getSymbol());

        for (QuoteDto quote: quotes) existingSymbols.remove(quote.getSymbol());

        DbProvider.getInstance().deleteQuotesByIds(new ArrayList<>(existingSymbols));
    }

    private List<QuoteDto> filterQuotes(int quoteType) {
        List<QuoteDto> filtered = new ArrayList<>();

        List<QuoteDto> quotes = mTickerSymbolsDto.getQuotes();

        for (QuoteDto quote: quotes) {
            if (quoteType == quote.getType().getNumVal()) {
                filtered.add(quote);
            }
        }

        return filtered;
    }
}
