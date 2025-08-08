package tl.bnctl.banking.data.accounts

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface AccountsService {

    @GET("accounts")
    suspend fun accounts(
        @Header("Authorization") token: String
    ): JsonObject

    @GET("accounts/{accountId}/statement")
    suspend fun accountStatement(
        @Header("Authorization") token: String,
        @Path("accountId") accountId: String,
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String,
        @Query("limit") limit: Int? = null
    ): JsonObject

    @GET("accounts/{accountId}")
    suspend fun accountDetails(
        @Header("Authorization") token: String,
        @Path("accountId") accountId: String
    ): JsonObject
}