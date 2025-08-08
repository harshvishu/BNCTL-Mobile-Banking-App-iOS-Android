package tl.bnctl.banking.data.eod

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Header

interface EoDService {

    @GET("checkEoD")
    suspend fun checkEoD(
        @Header("Authorization") token: String
    ): JsonObject

}