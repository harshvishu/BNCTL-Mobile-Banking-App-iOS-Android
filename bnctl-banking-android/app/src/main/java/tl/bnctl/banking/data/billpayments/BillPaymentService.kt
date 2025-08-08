package tl.bnctl.banking.data.billpayments

import com.google.gson.JsonObject
import retrofit2.http.*

interface BillPaymentService {

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @GET("bill/utility/fetch")
    suspend fun fetchBillPayments(
        @Header("Authorization") token: String,
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("bill/utility/validate")
    suspend fun validateBillPayment(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("bill/utility/approve")
    suspend fun approveBillPayment(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): JsonObject

}