package tl.bnctl.banking.data.templates

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*
import tl.bnctl.banking.data.templates.model.TemplateRequest

interface TemplatesService {

    @Headers("Content-Type: application/json")
    @GET("payees")
    suspend fun fetchTemplates(
        @Header("Authorization") token: String
    ): JsonObject

    @Headers("Content-Type: application/json")
    @DELETE("payees/{payeeId}")
    suspend fun deleteTemplate(
        @Header("Authorization") token: String,
        @Path("payeeId") payeeId: String
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @PUT("payees/{payeeId}")
    suspend fun editTemplate(
        @Header("Authorization") token: String,
        @Path("payeeId") payeeId: String,
        @Body templateRequest: TemplateRequest
    ): JsonObject


    @Headers("Content-Type: application/json")
    @POST("payees")
    suspend fun createTemplate(
        @Header("Authorization") token: String,
        @Body templateRequest: TemplateRequest
    ): JsonObject

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @GET("payees/banks")
    suspend fun fetchBanks(@Header("Authorization") token: String): JsonObject
}