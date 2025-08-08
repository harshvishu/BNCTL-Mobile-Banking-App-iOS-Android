package tl.bnctl.banking.data.exchange_rates

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ExchangeRatesService {

    @GET("exchange-rates")
    suspend fun fetchExchangeRates(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): JsonObject
}
