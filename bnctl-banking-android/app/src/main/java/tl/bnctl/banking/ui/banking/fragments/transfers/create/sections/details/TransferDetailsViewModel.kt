package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R

class TransferDetailsViewModel : ViewModel() {

    // mutable
    private val _amount: MutableLiveData<Double?> by lazy { MutableLiveData<Double?>() }
    private val _selectedCurrency: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val _availableCurrenciesResourceId: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    private val _reason: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val _type: MutableLiveData<TransferDetailsType> by lazy {
        MutableLiveData<TransferDetailsType>(
            TransferDetailsType.STANDARD
        )
    }

    // Let view model store the error messages so they can be updated easily from parent fragments
    // User resource ids
    private val _amountError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    private val _reasonError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }

    // Options
    private val _enableCurrencies: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(
            true
        )
    }
    private val _enableType: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(
            true
        )
    }

    // immutable
    val amount: LiveData<Double?> = _amount
    val selectedCurrency: LiveData<String> = _selectedCurrency
    val availableCurrenciesResourceId: LiveData<Int> = _availableCurrenciesResourceId
    val reason: LiveData<String> = _reason
    val type: LiveData<TransferDetailsType> = _type

    // Errors
    val amountError: LiveData<Int?> = _amountError
    val reasonError: LiveData<Int?> = _reasonError

    // Options
    val enableCurrencies: LiveData<Boolean> = _enableCurrencies
    val enableType: LiveData<Boolean> = _enableType

    fun validateAmount() {
        if (isAmountValid()) {
            clearAmountError()
        } else {
            raiseAmountError(R.string.error_transfer_validation_amount_greater_than_zero)
        }
    }

    fun validateReason() {
        if (isReasonValid()) {
            clearReasonError()
        } else {
            raiseReasonError(R.string.error_transfer_validation_required_reason)
        }
    }

    fun isCurrencyValid(): Boolean {
        return !selectedCurrency.value.isNullOrBlank()
    }

    fun setAmount(amount: Double?) {
        _amount.value = amount
    }

    fun selectCurrency(currency: String) {
        _selectedCurrency.value = currency
    }

    fun setReason(reason: String) {
        _reason.value = reason
    }

    fun changeType(type: TransferDetailsType) {
        _type.value = type
    }

    fun disableTypeOption() {
        _enableType.value = false
    }

    fun enableTypeOption() {
        _enableType.value = true
    }

    fun disableCurrenciesOption() {
        _enableCurrencies.value = false
    }

    fun enableCurrenciesOption() {
        _enableCurrencies.value = true
    }

    fun setAvailableCurrencies(currenciesResourceId: Int) {
        _availableCurrenciesResourceId.value = currenciesResourceId
    }

    private fun isReasonValid(): Boolean {
        return !reason.value.isNullOrBlank() &&
                (reason.value ?: "").length <= BuildConfig.TRANSFER_REASON_MAX_LENGTH
    }

    private fun isAmountValid(): Boolean {
        return amount.value != null && amount.value!! > 0
    }

    private fun isAmountValid(additionalValidation: (input: Double) -> Boolean): Boolean {
        return isAmountValid() && additionalValidation(amount.value!!)
    }

    fun raiseAmountError(errorMessage: Int) {
        _amountError.value = errorMessage
    }

    fun clearAmountError() {
        _amountError.value = null
    }

    private fun raiseReasonError(errorMessage: Int) {
        _reasonError.value = errorMessage
    }

    private fun clearReasonError() {
        _reasonError.value = null
    }
}