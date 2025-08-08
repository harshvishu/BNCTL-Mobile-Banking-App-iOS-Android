package tl.bnctl.banking.ui.fallback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FallbackConfirmActivityViewModel() : ViewModel() {

    private val _smsCode = MutableLiveData<String>()
    private val _pin = MutableLiveData<String>() // TODO: Maybe use this

    val smsCode: LiveData<String> = _smsCode
    val pin: LiveData<String> = _pin

    // Errors
    private val _smsCodeError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    private val _pinError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }

    val smsCodeError: LiveData<Int?> = _smsCodeError
    val pinError: LiveData<Int?> = _pinError

    fun setSmsCode(code: String) {
        _smsCode.postValue(code)
    }

    fun setPin(pin: String) {
        _pin.postValue(pin)
    }

    fun raiseSmsCodeError(errorMessage: Int) {
        _smsCodeError.value = errorMessage
    }

    fun clearSmsCodeError() {
        _smsCodeError.value = null
    }

    fun raisePinError(errorMessage: Int) {
        _pinError.value = errorMessage
    }

    fun clearPinError() {
        _pinError.value = null
    }
}