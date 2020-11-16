package ru.besttuts.stockwidget.sync;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.besttuts.stockwidget.sync.sparklab.dto.MobileQuoteShort;

/**
 * @author rchekashov
 * created on 04.10.2016
 */

public interface MyFinanceService {

    @POST("/api/quotes")
    Call<List<MobileQuoteShort>> quotes(@Body Set<Integer> ids);

    @POST("/api/quotes")
    Call<List<MobileQuoteShort>> quotes();

}
