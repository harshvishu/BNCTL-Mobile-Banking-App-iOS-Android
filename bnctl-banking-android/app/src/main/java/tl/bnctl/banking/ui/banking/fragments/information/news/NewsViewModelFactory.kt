package tl.bnctl.banking.ui.banking.fragments.information.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.news.NewsDataSource
import tl.bnctl.banking.data.news.NewsRepository
import tl.bnctl.banking.data.news.NewsService

class NewsViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(
                newsRepository = NewsRepository(
                    dataSource = NewsDataSource(getNewsService())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getNewsService(): NewsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(NewsService::class.java)
    }

}