package tl.bnctl.banking.data.current_user

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Header

interface CurrentUserService {

    @GET("users/current")
    suspend fun currentUser(@Header("Authorization") token: String): JsonObject

}