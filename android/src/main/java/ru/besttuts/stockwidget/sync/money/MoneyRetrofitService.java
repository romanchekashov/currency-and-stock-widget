package ru.besttuts.stockwidget.sync.money;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import ru.besttuts.stockwidget.sync.money.dto.QuoteDto;
import ru.besttuts.stockwidget.sync.money.dto.TickerFilterDto;
import ru.besttuts.stockwidget.sync.money.dto.TickerSymbolsDto;

/**
 * @author rchekashov
 *         created on 04.10.2016
 */
public interface MoneyRetrofitService {

    @GET("/api/v1/ticker/symbols")
    Call<TickerSymbolsDto> tickerSymbols();

    @POST("/api/v1/ticker/tape")
    Call<List<QuoteDto>> tickerTape(@Body TickerFilterDto filter);

}
