package ru.besttuts.stockwidget.sync;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.besttuts.stockwidget.model.QuoteLastTradeDate;
import ru.besttuts.stockwidget.sync.sparklab.QuoteDto;

/**
 * @author rchekashov
 *         created on 04.10.2016
 */

public interface MyFinanceService {

    @GET("api/quote-last-trade-dates")
    Call<List<QuoteLastTradeDate>> quotesWithLastTradeDate();

    @GET("api/quotes")
    Call<List<QuoteDto>> quotes(@Query(value = "symbols") String symbols);

}
