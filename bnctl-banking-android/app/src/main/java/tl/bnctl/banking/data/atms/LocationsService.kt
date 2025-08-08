package tl.bnctl.banking.data.atms

import com.google.gson.JsonObject
import retrofit2.http.GET

interface LocationsService {

    @GET("locations/atms")
    suspend fun fetchAtms(): JsonObject

    @GET("locations/branches")
    suspend fun fetchBranches(): JsonObject
}