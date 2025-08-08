package tl.bnctl.banking.ui.banking.fragments.transfers.templates.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.templates.TemplatesRepository

class TemplateDetailsViewModel(
    private val templatesRepository: TemplatesRepository
) : ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _deleteResult: MutableLiveData<Boolean> = MutableLiveData()
    val deleteResult: LiveData<Boolean> = _deleteResult

    private fun setLoading(isLoading: Boolean) {
        this._isLoading.postValue(isLoading)
    }

    fun deleteTemplate(payeeId: String) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = templatesRepository.deleteTemplate(payeeId)
            _deleteResult.postValue(result is Result.Success<*>)
            setLoading(false)
        }
    }
}