package tl.bnctl.banking.ui.banking.fragments.transfers.templates

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.templates.TemplatesRepository
import tl.bnctl.banking.data.templates.model.Bank
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.data.templates.model.TemplateRequest
import tl.bnctl.banking.data.templates.model.TemplateTransferType
import tl.bnctl.banking.util.LocaleHelper

class CreateEditTemplateViewModel(
    private val templatesRepository: TemplatesRepository
) : ViewModel() {
    // region Request fields

    private val _accountTypeId: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val accountTypeId: LiveData<String> = _accountTypeId

    private val _payeeName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val payeeName: LiveData<String> = _payeeName

    private val _payeeEmail: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val payeeEmail: LiveData<String> = _payeeEmail

    private val _accountNumber: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val accountNumber: LiveData<String> = _accountNumber

    private val _currency: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val currency: LiveData<String> = _currency

    private val _bank: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val bank: LiveData<String> = _bank

    private val _swift: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val swift: LiveData<String> = _swift

    // endregion

    private val _result = MutableLiveData<Result<Template>>().apply {}
    val result: LiveData<Result<Template>> = _result

    private val _banksResult = MutableLiveData<Result<List<Bank>>>().apply {}
    val banksResult: LiveData<Result<List<Bank>>> = _banksResult

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _availableCurrenciesResourceId: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val availableCurrenciesResourceId: LiveData<Int> = _availableCurrenciesResourceId

    private val _enableCurrencies: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(
            true
        )
    }
    val enableCurrencies: LiveData<Boolean> = _enableCurrencies

    // region Errors

    private val _accountTypeIdError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val accountTypeIdError: LiveData<Int?> = _accountTypeIdError

    private val _payeeNameError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val payeeNameError: LiveData<Int?> = _payeeNameError

    private val _payeeEmailError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val payeeEmailError: LiveData<Int?> = _payeeEmailError

    private val _accountNumberError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val accountNumberError: LiveData<Int?> = _accountNumberError

    private val _currencyError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val currencyError: LiveData<Int?> = _currencyError

    // endregion

    fun fetchBanks() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = templatesRepository.fetchBanks()
            _isLoading.postValue(false)
            _banksResult.postValue(result)
        }
    }

    fun sendCreateRequest(context: Context) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = templatesRepository.createTemplate(
                TemplateRequest(
                    "bank", // BNCTL *currently* doesn't support non-bank templates
                    accountNumber.value,
                    accountTypeId.value,
                    payeeName.value,
                    null,
                    null,
                    null,
                    bank.value,
                    swift.value,
                    payeeEmail.value,
                    currency.value,
                    LocaleHelper.getCurrentLanguage(context),
                    true
                )
            )
            _isLoading.postValue(false)
            _result.postValue(result)
        }
    }

    fun sendEditRequest(payeeId: String, context: Context) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = templatesRepository.editTemplate(
                payeeId,
                TemplateRequest(
                    "bank", // BNCTL *currently* doesn't support non-bank templates
                    accountNumber.value,
                    accountTypeId.value,
                    payeeName.value,
                    null,
                    null,
                    null,
                    bank.value,
                    swift.value,
                    payeeEmail.value,
                    currency.value,
                    LocaleHelper.getCurrentLanguage(context),
                    true
                )
            )
            _isLoading.postValue(false)
            _result.postValue(result)
        }
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

    fun setAccountTypeId(templateTransferType: TemplateTransferType) {
        _accountTypeId.value = templateTransferType.id
    }

    fun setAccountTypeId(value: String) {
        _accountTypeId.value = value
    }

    private fun raiseAccountTypeIdError(errorMessage: Int) {
        _accountTypeIdError.value = errorMessage
    }

    private fun clearAccountTypeIdError() {
        _accountTypeIdError.value = null
    }

    fun validateAccountTypeId() {
        if (accountTypeId.value.isNullOrBlank()) {
            raiseAccountTypeIdError(R.string.common_error_field_required)
        } else {
            clearAccountTypeIdError()
        }
    }

    fun setPayeeName(value: String) {
        _payeeName.value = value
    }

    private fun raisePayeeNameError(errorMessage: Int) {
        _payeeNameError.value = errorMessage
    }

    private fun clearPayeeNameError() {
        _payeeNameError.value = null
    }

    fun validatePayeeName() {
        if (payeeName.value.isNullOrBlank()) {
            raisePayeeNameError(R.string.common_error_field_required)
        } else {
            clearPayeeNameError()
        }
    }

    fun setPayeeEmail(value: String) {
        _payeeEmail.value = value
    }

    private fun raisePayeeEmailError(errorMessage: Int) {
        _payeeEmailError.value = errorMessage
    }

    private fun clearPayeeEmailError() {
        _payeeEmailError.value = null
    }

    fun validatePayeeEmail() {
        val emailValid = if (!payeeEmail.value.isNullOrBlank()) {
            Patterns.EMAIL_ADDRESS.matcher(payeeEmail.value!!).matches()
        } else {
            true
        }
        if (emailValid) {
            clearPayeeEmailError()
        } else {
            raisePayeeEmailError(R.string.error_transfer_validation_email_invalid)
        }
    }

    fun setAccountNumber(value: String) {
        _accountNumber.value = value
    }

    private fun raiseAccountNumberError(errorMessage: Int) {
        _accountNumberError.value = errorMessage
    }

    private fun clearAccountNumberError() {
        _accountNumberError.value = null
    }

    fun validateAccountNumber() {
        if (accountNumber.value.isNullOrBlank()) {
            raiseAccountNumberError(R.string.common_error_field_required)
        } else {
            clearAccountNumberError()
        }
    }

    fun setCurrency(value: String) {
        _currency.value = value
    }

    private fun raiseCurrencyError(errorMessage: Int) {
        _currencyError.value = errorMessage
    }

    private fun clearCurrencyError() {
        _currencyError.value = null
    }

    fun validateCurrency() {
        if (currency.value.isNullOrBlank()) {
            raiseCurrencyError(R.string.common_error_field_required)
        } else {
            clearCurrencyError()
        }
    }

    fun setBank(value: String) {
        _bank.value = value
    }

    fun setSwift(value: String) {
        _swift.value = value
    }

    fun setBank(bank: Bank) {
        setBank(bank.name)
        setSwift(bank.swift)
    }
}