package tl.bnctl.banking.data.news

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.news.model.News

class NewsRepository(
    private val dataSource: NewsDataSource
) {
    var news: List<News>? = null

    suspend fun fetchNews(): Result<List<News>> {
        val result = dataSource.fetchNews()
        if (result is Result.Success) {
            news = result.data
        }
        return result
    }

}