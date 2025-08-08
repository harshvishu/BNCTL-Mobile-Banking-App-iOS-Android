package tl.bnctl.banking.data.config

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface AppConfigService {

    @GET("config/mobile/android")
    suspend fun getAppConfig(
        @Query("currentVersion") currentVersion: String,
        @Query("currentVersionCode") versionCode: Int
    ): JsonObject

}