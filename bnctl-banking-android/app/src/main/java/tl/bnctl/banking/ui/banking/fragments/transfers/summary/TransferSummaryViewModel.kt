package tl.bnctl.banking.ui.banking.fragments.transfers.summary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.payee.model.Payee
import tl.bnctl.banking.data.templates.model.TemplateRequest
import tl.bnctl.banking.data.transfers.TransferRepository
import tl.bnctl.banking.data.transfers.enums.TransferExecution
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.data.transfers.model.TransferCreateResult
import tl.bnctl.banking.data.transfers.model.TransferSummary

class TransferSummaryViewModel(
    private val transferRepository: TransferRepository
) : ViewModel() {

    private val _TAG = TransferSummaryViewModel::class.simpleName

    private val _amount: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val amount: LiveData<Double> = _amount

    private val _currency: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val currency: LiveData<String> = _currency

    private val _from: MutableLiveData<Account> by lazy { MutableLiveData<Account>() }
    val from: LiveData<Account> = _from

    private val _to: MutableLiveData<Payee> by lazy { MutableLiveData<Payee>() }
    val to: LiveData<Payee> = _to

    private val _reason: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val reason: LiveData<String> = _reason

    private val _feeAmount: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val feeAmount: LiveData<Double> = _feeAmount

    private val _feeCurrency: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val feeCurrency: LiveData<String> = _feeCurrency

    private val _transferType: MutableLiveData<TransferType> by lazy { MutableLiveData<TransferType>() }
    val transferType: LiveData<TransferType> = _transferType

    private val _executionType: MutableLiveData<TransferExecution> by lazy { MutableLiveData<TransferExecution>() }
    val executionType: LiveData<TransferExecution> = _executionType

    private val _newPayee: MutableLiveData<TemplateRequest?> by lazy { MutableLiveData<TemplateRequest?>() }
    val newPayee: LiveData<TemplateRequest?> = _newPayee

    private val _additionalDetails: MutableLiveData<Map<String, Any>> by lazy {
        MutableLiveData<Map<String, Any>>(
            mapOf()
        )
    }
    val additionalDetails: LiveData<Map<String, Any>> = _additionalDetails

    private val _createResult = MutableLiveData<TransferCreateResult>()
    val creationResult: LiveData<TransferCreateResult> = _createResult

    fun startTransferCreation() {
        viewModelScope.launch(Dispatchers.IO) {
            // Request from the repository to login.
            val result = transferRepository.createTransfer(
                TransferSummary(
                    amount.value!!,
                    currency.value!!,
                    feeAmount.value!!,
                    feeCurrency.value!!,
                    from.value!!,
                    to.value!!,
                    reason.value!!,
                    transferType.value!!,
                    executionType.value!!,
                    additionalDetails.value!!,
                    newPayee = newPayee.value
                )
            )
            if (result is Result.Success) {
                _createResult.postValue(TransferCreateResult(success = result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _createResult.postValue(TransferCreateResult(error = errorResult))
                Log.e(_TAG, errorResult.message)
            }
        }
    }

    fun setAmount(amount: Double) {
        _amount.value = amount
    }

    fun setCurrency(currency: String) {
        _currency.value = currency
    }

    fun setFrom(sourceAccount: Account) {
        _from.value = sourceAccount
    }

    fun setTo(destinationAccount: Payee) {
        _to.value = destinationAccount
    }

    fun setReason(reason: String) {
        _reason.value = reason
    }

    fun setFeeAmount(feeAmount: Double) {
        _feeAmount.value = feeAmount
    }

    fun setFeeCurrency(feeCurrency: String) {
        _feeCurrency.value = feeCurrency
    }

    fun setTransferType(transferType: TransferType) {
        _transferType.value = transferType
    }

    fun setExecutionType(executionType: TransferExecution) {
        _executionType.value = executionType
    }

    fun setAdditionalDetails(additionalDetails: Map<String, Any>) {
        _additionalDetails.value = additionalDetails
    }

    fun setNewPayee(newPayee: TemplateRequest?) {
        _newPayee.value = newPayee
    }
}