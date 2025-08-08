package tl.bnctl.banking.data.news

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.news.model.News
import java.text.SimpleDateFormat
import java.util.*

class NewsDataSource(
    private val newsService: NewsService
) {
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy")

    suspend fun fetchNews(): Result<List<News>> {
        try {
            val newsResult: JsonArray
            newsService.fetchNews().also { newsResult = it }
            val news = ArrayList<News>()
            for (newsJsonObject in newsResult) {
                transformJsonToNewsObj(newsJsonObject.asJsonObject, news, dateFormat)
            }
            return Result.Success(news)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    private fun transformJsonToNewsObj(
        news: JsonObject,
        listNews: MutableList<News>,
        dateFormat: SimpleDateFormat
    ) {
        val dateStr = news.get("date").asString
        val date: Date = dateFormat.parse(dateStr)
        listNews.add(
            News(
                news.get("id").asString,
                date,
                news.get("title")?.asString,
                news.get("text").asString,
                news.get("url")?.asString
            )
        )
    }
}
