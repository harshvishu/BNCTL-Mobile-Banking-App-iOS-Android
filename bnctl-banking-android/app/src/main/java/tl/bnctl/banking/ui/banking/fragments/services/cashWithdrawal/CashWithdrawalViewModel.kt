package tl.bnctl.banking.ui.banking.fragments.services.cashWithdrawal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.branches.BranchRepository
import tl.bnctl.banking.data.branches.model.Branch
import tl.bnctl.banking.data.cashWithdrawal.CashWithdrawalDataSource
import tl.bnctl.banking.data.cashWithdrawal.model.CashWithdrawal
import tl.bnctl.banking.data.cashWithdrawal.model.CashWithdrawalRequest
import tl.bnctl.banking.ui.utils.DateUtils
import java.math.BigDecimal
import java.util.*

class CashWithdrawalViewModel(
    private val branchesRepository: BranchRepository,
    private val cashWithdrawalDataSource: CashWithdrawalDataSource
) : ViewModel() {

    private val _cashWithdrawalResult = MutableLiveData<Result<CashWithdrawal>>()

    private val _amount = MutableLiveData<BigDecimal?>()
    private val _currency = MutableLiveData<String>()
    private val _location = MutableLiveData<String>()
    private val _date = MutableLiveData<Date?>()
    private val _description = MutableLiveData<String>()
    private val _branches = MutableLiveData<Result<List<Branch>>>().apply {}

    private val _amountError = MutableLiveData<Int?>()
    private val _locationError = MutableLiveData<Int?>()
    private val _dateError = MutableLiveData<Int?>()

    val cashWithdrawal: LiveData<Result<CashWithdrawal>> = _cashWithdrawalResult

    val amount: LiveData<BigDecimal?> = _amount
    val currency: LiveData<String> = _currency
    val location: LiveData<String> = _location
    val date: LiveData<Date?> = _date
    val description: LiveData<String> = _description
    val branches: LiveData<Result<List<Branch>>> = _branches

    val amountError: LiveData<Int?> = _amountError
    val locationError: LiveData<Int?> = _locationError
    val dateError: LiveData<Int?> = _dateError

    fun fetchBranches() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = branchesRepository.fetchBranches()
            _branches.postValue(result)
        }
    }

    fun requestWithdrawal() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = cashWithdrawalDataSource.withdrawCash(
                CashWithdrawalRequest(
                    amount.value!!.toPlainString(),
                    currency.value!!,
                    DateUtils.formatDateISO(date.value!!),
                    location.value!!,
                    description.value ?: ""
                )
            )
            _cashWithdrawalResult.postValue(result)
        }
    }

    fun validateAmount() {
        if (isAmountValid()) {
            clearAmountError()
        } else {
            raiseAmountError(R.string.error_transfer_validation_amount_greater_than_zero)
        }
    }

    fun validateLocation() {
        if (isLocationValid()) {
            clearLocationError()
        } else {
            raiseLocationError(R.string.error_cash_withdrawal_location)
        }
    }

    fun validateDate() {
        if (isDateValid()) {
            clearDateError()
        } else {
            raiseDateError(R.string.error_cash_withdrawal_date)
        }
    }

    private fun isAmountValid(): Boolean {
        return amount.value != null && amount.value!! > BigDecimal.ZERO
    }

    private fun isLocationValid(): Boolean {
        return location.value != null
    }

    private fun isDateValid(): Boolean {
        return date.value != null
    }

    fun setAmount(amount: BigDecimal) {
        _amount.value = amount
    }

    fun selectCurrency(currency: String) {
        _currency.value = currency
    }

    fun setLocation(location: String) {
        _location.value = location
    }

    fun setDate(date: Date) {
        _date.value = date
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun raiseAmountError(amountErrorId: Int) {
        _amountError.value = amountErrorId
    }

    fun clearAmountError() {
        _amountError.value = null
    }

    fun raiseLocationError(locationErrorId: Int) {
        _locationError.value = locationErrorId
    }

    fun clearLocationError() {
        _locationError.value = null
    }

    fun raiseDateError(dateErrorId: Int) {
        _dateError.value = dateErrorId
    }

    fun clearDateError() {
        _dateError.value = null
    }

}