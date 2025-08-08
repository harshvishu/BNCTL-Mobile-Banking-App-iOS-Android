package tl.bnctl.banking.data.transfers

import com.google.gson.JsonObject
import retrofit2.http.*

interface TransferService {

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @GET("transfers/internet-banking")
    suspend fun fetchTransfers(
        @Header("Authorization") token: String,
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("transfers/validate")
    suspend fun validateTransfer(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("transfers/create")
    suspend fun createTransfer(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("transfers/execute")
    suspend fun createAndExecuteTransfer(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): JsonObject

    @JvmSuppressWildcards
    @POST("transfers/confirm")
    @FormUrlEncoded
    suspend fun confirmTransfer(
        @Header("Authorization") token: String,
        @Field("validationRequestId") validationRequestId: String,
        @Field("secret") secret: String,
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("transfers/create/pending")
    suspend fun createPendingTransfer(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): JsonObject
}