package tl.bnctl.banking.data.branches

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Headers

interface BranchService {

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @GET("branches")
    suspend fun fetchBranches(): JsonObject
}