package tl.bnctl.banking.ui.banking.fragments.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.login.LoginRepository

class InformationViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _logoutFinished: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val logoutStatus: LiveData<Boolean> = _logoutFinished

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }


    fun logout() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            loginRepository.logout()
            _logoutFinished.postValue(true)
        }
    }
}