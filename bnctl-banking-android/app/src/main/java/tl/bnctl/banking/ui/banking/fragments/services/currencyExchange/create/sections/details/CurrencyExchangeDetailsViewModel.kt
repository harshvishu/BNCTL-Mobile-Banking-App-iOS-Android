package tl.bnctl.banking.ui.banking.fragments.services.currencyExchange.create.sections.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tl.bnctl.banking.R
import tl.bnctl.banking.data.currencyExchange.enums.CurrencyExchangeOperationType

class CurrencyExchangeDetailsViewModel : ViewModel() {

    private val _amount: MutableLiveData<Double?> by lazy { MutableLiveData<Double?>() }
    val amount: LiveData<Double?> = _amount

    private val _selectedCurrency: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val selectedCurrency: LiveData<String> = _selectedCurrency

    private val _type: MutableLiveData<CurrencyExchangeOperationType> by lazy {
        MutableLiveData<CurrencyExchangeOperationType>(
            CurrencyExchangeOperationType.SELL
        )
    }
    val type: LiveData<CurrencyExchangeOperationType> = _type

    private val _preferentialRatePin: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val preferentialRatePin: LiveData<String> = _preferentialRatePin

    private val _amountError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val amountError: LiveData<Int?> = _amountError

    private val _preferentialRatesPinError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val preferentialRatesPinError: LiveData<Int?> = _preferentialRatesPinError

    fun validateAmount() {
        if (isAmountValid()) {
            clearAmountError()
        } else {
            raiseAmountError(R.string.error_transfer_validation_amount_greater_than_zero)
        }
    }

    fun validatePreferentialRatesPin() {
        if (isPreferentialRatesPinValid()) {
            clearPreferentialRatesPinError()
        } else {
            raisePreferentialRatesPinError(R.string.error_transfer_validation_preferential_rates_pin_length)
        }
    }

    fun setAmount(amount: Double?) {
        _amount.value = amount
    }

    fun selectCurrency(currency: String) {
        _selectedCurrency.value = currency
    }

    fun changeType(type: CurrencyExchangeOperationType) {
        _type.value = type
    }

    fun setPreferentialRatesPin(pin: String) {
        _preferentialRatePin.value = pin.trim()
    }

    private fun isAmountValid(): Boolean {
        return amount.value != null && amount.value!! > 0
    }

    private fun isPreferentialRatesPinValid(): Boolean {
        return preferentialRatePin.value.isNullOrBlank() || preferentialRatePin.value!!.length <= 10
    }

    fun isCurrencyValid(): Boolean {
        return !selectedCurrency.value.isNullOrBlank()
    }

    private fun raiseAmountError(errorMessage: Int) {
        _amountError.value = errorMessage
    }

    private fun raisePreferentialRatesPinError(errorMessage: Int) {
        _preferentialRatesPinError.value = errorMessage
    }

    private fun clearAmountError() {
        _amountError.value = null
    }

    private fun clearPreferentialRatesPinError() {
        _preferentialRatesPinError.value = null
    }

}