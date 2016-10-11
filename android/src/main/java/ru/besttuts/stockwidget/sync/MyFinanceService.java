package ru.besttuts.stockwidget.sync;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.besttuts.stockwidget.model.QuoteLastTradeDate;

/**
 * @author rchekashov
 *         created on 04.10.2016
 */

public interface MyFinanceService {

    @GET("api/quote-last-trade-date")
    Call<List<QuoteLastTradeDate>> quotesWithLastTradeDate();

}
