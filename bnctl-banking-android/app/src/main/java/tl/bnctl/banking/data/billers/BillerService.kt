package tl.bnctl.banking.data.billers

import com.google.gson.JsonArray
import retrofit2.http.GET
import retrofit2.http.Header

interface BillerService {

    @GET("bill/my-billers")
    suspend fun myBillers(@Header("Authorization") token: String): JsonArray

}