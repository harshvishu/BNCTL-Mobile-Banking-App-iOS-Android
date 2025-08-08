package tl.bnctl.banking.data.login

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface LoginService {

    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): JsonObject

    @POST("auth/refresh")
    fun refreshToken(@Body body: Map<String, String>): Call<JsonObject>

    @GET("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>

    @PUT("users/current/password/change")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): JsonObject

    @PUT("users/current/username/change")
    suspend fun changeUsername(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): JsonObject


    @POST("auth/fallback/sendSmsv")
    suspend fun sendFallbackSMS(@Body body: Map<String, String>): JsonObject

    @POST("auth/fallback/confirm")
    suspend fun confirmFallback(@Body body: Map<String, String>): JsonObject

    @GET("users/current/accessPolicy")
    suspend fun fetchAccessPolicy(@Header("Authorization") accessToken: String): JsonObject

}