package tl.bnctl.banking.data.news

import com.google.gson.JsonArray
import retrofit2.http.GET

interface NewsService {

    @GET("news")
    suspend fun fetchNews(): JsonArray
}