package ru.besttuts.stockwidget.sync;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.besttuts.stockwidget.sync.model.YahooMultiQueryData;

/**
 * @author rchekashov
 *         created on 04.10.2016
 */

public interface YahooFinanceService {

    @GET("yql?format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<YahooMultiQueryData> yahooMultiQueryData(@Query(value = "q") String query);

}
