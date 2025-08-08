package tl.bnctl.banking.data.customer

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Header

interface CustomerService {

    @GET("accounts/groups")
    suspend fun fetchCustomers(
        @Header("Authorization") token: String
    ): JsonObject

}
