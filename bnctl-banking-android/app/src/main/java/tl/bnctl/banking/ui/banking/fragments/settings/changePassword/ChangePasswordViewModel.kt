package tl.bnctl.banking.ui.banking.fragments.settings.changePassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.login.LoginRepository
import tl.bnctl.banking.data.login.model.accsessPolicy.AccessPolicy

class ChangePasswordViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _changePasswordResult: MutableLiveData<Result<Boolean>?> by lazy { MutableLiveData<Result<Boolean>?>() }
    val changePasswordResult: LiveData<Result<Boolean>?> = _changePasswordResult

    private val _accessPolicyResult: MutableLiveData<Result<AccessPolicy>?> by lazy { MutableLiveData<Result<AccessPolicy>?>() }
    val accessPolicyResult: LiveData<Result<AccessPolicy>?> = _accessPolicyResult

    private val _loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val loading: LiveData<Boolean> = _loading

    private val _repeatPasswordError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val repeatPasswordError: LiveData<Int?> = _repeatPasswordError

    private val _passwordError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val passwordError: LiveData<Int?> = _passwordError

    private val _currentPasswordError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val currentPasswordError: LiveData<Int?> = _currentPasswordError


    fun changePassword(username: String, oldPassword: String, newPassword: String) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(false)
            val result = loginRepository.changePassword(username, oldPassword, newPassword);
            _changePasswordResult.postValue(result)
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

    fun raiseCurrentPasswordError(errorMessage: Int) {
        _currentPasswordError.value = errorMessage
    }

    fun clearCurrentPasswordError() {
        _currentPasswordError.value = null
    }


    fun raisePasswordError(errorMessage: Int) {
        _passwordError.value = errorMessage
    }

    fun clearPasswordError() {
        _passwordError.value = null
    }

    fun raiseRepeatPasswordError(errorMessage: Int) {
        _repeatPasswordError.value = errorMessage
    }

    fun clearRepeatPasswordError() {
        _repeatPasswordError.value = null
    }

    fun dirtyLogout() {
        loginRepository.dirtyLogout()
    }
}