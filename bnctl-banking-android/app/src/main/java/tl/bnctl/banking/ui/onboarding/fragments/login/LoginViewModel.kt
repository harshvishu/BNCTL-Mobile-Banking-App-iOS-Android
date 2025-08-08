package tl.bnctl.banking.ui.onboarding.fragments.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.current_user.CurrentUserRepository
import tl.bnctl.banking.data.fallback.SendFallbackSMSResult
import tl.bnctl.banking.data.login.LoginRepository
import tl.bnctl.banking.data.login.model.LoggedInUser
import tl.bnctl.banking.services.AuthenticationService

/**
 * ViewModel casts generic Result to Specific Result class.
 * Result -> LoginResult
 */
class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val currentUserRepository: CurrentUserRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult?>()
    private val _fallbackSMSResult = MutableLiveData<Result<SendFallbackSMSResult>?>()
    private val _currentUserResult = MutableLiveData<LoggedInUser?>()

    private val _usernameError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    private val _passwordError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }

    val loginResult: LiveData<LoginResult?> = _loginResult
    val currentUserResult: LiveData<LoggedInUser?> = _currentUserResult
    val fallbackSMSResult: LiveData<Result<SendFallbackSMSResult>?> = _fallbackSMSResult

    // Used for telling the fallback confirm screen that PIN is required
    private val _usePin = MutableLiveData<Boolean>().apply { value = false }
    val usePin: LiveData<Boolean> = _usePin

    // Errors
    val usernameError: LiveData<Int?> = _usernameError
    val passwordError: LiveData<Int?> = _passwordError


    // Network operations
    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Clear previous login result
            _loginResult.postValue(LoginResult(null, null))
            // Request from the repository to login.
            val result = loginRepository.login(username, password)
            if (result is Result.Success) {
                AuthenticationService.getInstance().saveAuthenticationData(result.data)
                _loginResult.postValue(LoginResult(success = result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _loginResult.postValue(LoginResult(error = errorResult))
                Log.e(TAG, errorResult.message)
            }
        }
    }

    fun sendFallbackSMS(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository.sendFallbackSMS(username, password)
            if (result is Result.Success) {
                _fallbackSMSResult.postValue(result)
            } else {
                _fallbackSMSResult.postValue(result)
                Log.e(
                    TAG,
                    "Error sending fallback SMS: ${(result as Result.Error).getErrorString()}"
                )
            }
        }
    }

    fun confirmFallback(
        username: String,
        password: String,
        smsCode: String,
        pin: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository.confirmFallback(username, password, smsCode, pin)
            if (result is Result.Success) {
                AuthenticationService.getInstance().saveAuthenticationData(result.data)
                _loginResult.postValue(LoginResult(success = result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _loginResult.postValue(LoginResult(error = errorResult))
                Log.e(TAG, errorResult.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.logout()
        }
    }

    fun checkCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = currentUserRepository.getCurrentUser()
            if (currentUser is Result.Success) {
                _currentUserResult.postValue(currentUser.data)
            } else {
                _currentUserResult.postValue(null)
            }
        }
    }

    // Validations
    fun raiseUsernameError(errorMessage: Int) {
        _usernameError.value = errorMessage
    }

    fun raisePasswordError(errorMessage: Int) {
        _passwordError.value = errorMessage
    }

    fun clearUsernameError() {
        _usernameError.value = null
    }

    fun clearPasswordError() {
        _passwordError.value = null
    }

    /**
     * This is needed because of the way we do the login now. If you move around the fallback-related screens, the state of things gets messed up
     */
    fun resetLoginState() {
        _loginResult.value = null
        _fallbackSMSResult.value = null
    }

    fun setUsePin(usePin: Boolean) {
        _usePin.value = usePin
    }

    companion object {
        @JvmField
        val TAG: String? = LoginViewModel::class.simpleName
    }

}