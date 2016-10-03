package ru.besttuts.stockwidget.sync;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.besttuts.stockwidget.sync.model.YahooMultiQueryData;

/**
 * @author rchekashov
 *         created on 04.10.2016
 */

public interface YahooFinanceService {

    @GET("SELECT%20*%20FROM%20query.multi%20WHERE%20queries%3D%22select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20({xchange});select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20({quotes})%3B%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<YahooMultiQueryData> yahooMultiQueryData(
            @Path("xchange") String xchange,
            @Path("quotes") String quotes);

}
