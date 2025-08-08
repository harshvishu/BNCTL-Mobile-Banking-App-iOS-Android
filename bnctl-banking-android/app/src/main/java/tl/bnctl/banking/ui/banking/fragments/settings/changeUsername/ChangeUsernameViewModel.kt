package tl.bnctl.banking.ui.banking.fragments.settings.changeUsername

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.login.LoginRepository
import tl.bnctl.banking.data.login.model.accsessPolicy.AccessPolicy

class ChangeUsernameViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _changeUsernameResult: MutableLiveData<Result<Boolean>?> by lazy { MutableLiveData<Result<Boolean>?>() }
    val changeUsernameResult: LiveData<Result<Boolean>?> = _changeUsernameResult

    private val _accessPolicyResult: MutableLiveData<Result<AccessPolicy>?> by lazy { MutableLiveData<Result<AccessPolicy>?>() }
    val accessPolicyResult: LiveData<Result<AccessPolicy>?> = _accessPolicyResult

    private val _loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val loading: LiveData<Boolean> = _loading

    private val _repeatUsernameError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val repeatUsernameError: LiveData<Int?> = _repeatUsernameError

    private val _usernameError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val usernameError: LiveData<Int?> = _usernameError

    fun changeUsername(oldUsername: String, newUsername: String) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(false)
            val result = loginRepository.changeUsername(oldUsername, newUsername)
            _changeUsernameResult.postValue(result)
        }
    }

    fun fetchAccessPolicy() {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository.fetchAccessPolicy()
            _loading.postValue(false)
            _accessPolicyResult.postValue(result)
        }
    }

    fun raiseUsernameError(errorMessage: Int) {
        _usernameError.value = errorMessage
    }

    fun clearUsernameError() {
        _usernameError.value = null
    }

    fun raiseRepeatUsernameError(errorMessage: Int) {
        _repeatUsernameError.value = errorMessage
    }

    fun clearRepeatUsernameError() {
        _repeatUsernameError.value = null
    }

    fun dirtyLogout() {
        loginRepository.dirtyLogout()
    }
}