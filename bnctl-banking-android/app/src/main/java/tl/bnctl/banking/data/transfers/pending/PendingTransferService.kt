package tl.bnctl.banking.data.transfers.pending

import com.google.gson.JsonObject
import retrofit2.http.*

interface PendingTransferService {

    @Headers("Content-Type: application/json")
    @GET("transfers/pending")
    suspend fun fetchPendingTransfers(
        @Header("Authorization") token: String
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("transfers/approve")
    suspend fun confirmPendingTransfers(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("transfers/reject")
    suspend fun rejectPendingTransfers(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): JsonObject
}