package tl.bnctl.banking.data.cards

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import tl.bnctl.banking.data.cards.model.CardStatementRequest
import tl.bnctl.banking.data.cards.model.NewDebitCardRequest

interface CardsService {

    @GET("cards")
    suspend fun cards(@Header("Authorization") token: String): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @GET("cards/products")
    suspend fun fetchCardProducts(
        @Header("Authorization") token: String
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("cards")
    suspend fun newDebitCardRequest(
        @Header("Authorization") token: String,
        @Body request: NewDebitCardRequest
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("cards/statement")
    suspend fun fetchCardStatements(
        @Header("Authorization") token: String,
        @Body request: CardStatementRequest
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @GET("cards/creditCardStatement")
    suspend fun fetchCreditCardStatements(
        @Header("Authorization") token: String,
        @Query("cardNumber") cardNumber: String,
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @GET("cards/creditCardStatement/download")
    @Streaming
    suspend fun downloadCreditCardStatement(
        @Header("Authorization") token: String,
        @Query("fileName") fileName: String
    ): Response<ResponseBody>
}