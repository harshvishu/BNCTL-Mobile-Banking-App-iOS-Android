package tl.bnctl.banking.data.cashWithdrawal

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface CashWithdrawalService {

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("cash-withdrawal")
    suspend fun requestCashWithdrawal(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): JsonObject

}