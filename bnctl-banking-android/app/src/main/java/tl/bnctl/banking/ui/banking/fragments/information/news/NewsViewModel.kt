package tl.bnctl.banking.ui.banking.fragments.information.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.news.NewsRepository
import tl.bnctl.banking.data.news.model.News

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private var _news = MutableLiveData<Result<List<News>>?>()
    val news: LiveData<Result<List<News>>?> = _news

    fun fetchNews() {
        if (_news.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                val result: Result<List<News>> = newsRepository.fetchNews()
                _news.postValue(result)
            }
        }
    }

    fun clearData() {
        _news.postValue(null)
    }
}