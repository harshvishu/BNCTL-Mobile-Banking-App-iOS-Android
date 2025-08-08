package tl.bnctl.banking.data.insurance

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface InsuranceService {

    @Headers("Content-Type: application/json")
    @GET("insurance")
    suspend fun fetchInsurances(
        @Header("Authorization") token: String
    ): JsonObject
}