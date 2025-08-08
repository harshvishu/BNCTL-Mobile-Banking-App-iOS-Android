package tl.bnctl.banking.ui.banking.fragments.services.insurance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.accounts.model.Beneficiary
import tl.bnctl.banking.data.insurance.InsuranceRepository
import tl.bnctl.banking.data.insurance.model.Insurance
import tl.bnctl.banking.data.insurance.model.PaymentCurrency
import tl.bnctl.banking.data.payee.model.Payee
import tl.bnctl.banking.data.transfers.TransferRepository
import tl.bnctl.banking.data.transfers.enums.TransferExecution
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.data.transfers.model.TransferCreateResult
import tl.bnctl.banking.data.transfers.model.TransferSummary
import tl.bnctl.banking.ui.banking.fragments.transfers.summary.TransferSummaryViewModel

class InsuranceViewModel(
    private val insuranceRepository: InsuranceRepository,
    private val transferRepository: TransferRepository
) : ViewModel() {

    private val _TAG = TransferSummaryViewModel::class.simpleName

    private val _insurances = MutableLiveData<Result<List<Insurance>>>()
    val insurances: LiveData<Result<List<Insurance>>> = _insurances

    private val _selectedSourceAccount = MutableLiveData<Account>()
    val selectedSourceAccount: LiveData<Account> = _selectedSourceAccount

    private val _selectedInsurance = MutableLiveData<Insurance?>()
    val selectedInsurance: LiveData<Insurance?> = _selectedInsurance

    private val _transferCreateResult = MutableLiveData<TransferCreateResult>()
    val transferCreateResult: LiveData<TransferCreateResult> = _transferCreateResult

    private val _selectedPaymentCurrency = MutableLiveData<PaymentCurrency?>()
    val selectedPaymentCurrency: LiveData<PaymentCurrency?> = _selectedPaymentCurrency

    fun fetchInsurances() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = insuranceRepository.fetchInsurances()
            _insurances.postValue(result)
        }
    }

    fun setSelectedSourceAccount(result: Result<List<Account>>) {
        if (result is Result.Success && result.data.isNotEmpty()) {
            if (_selectedSourceAccount.value == null) {
                _selectedSourceAccount.postValue(result.data[0])
            }
        }
    }

    fun selectInsurance(insurance: Insurance) {
        _selectedInsurance.postValue(insurance)
    }

    fun selectSourceAccount(account: Account) {
        _selectedSourceAccount.postValue(account)
    }

    fun startTransferCreation() {
        viewModelScope.launch(Dispatchers.IO) {
            // Request from the repository to login.
            val result = transferRepository.createTransfer(
                generateTransferSummary()
            )
            if (result is Result.Success) {
                _transferCreateResult.postValue(TransferCreateResult(success = result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _transferCreateResult.postValue(TransferCreateResult(error = errorResult))
                Log.e(_TAG, errorResult.message)
            }
        }
    }

    fun generateTransferSummary(): TransferSummary {
        val selectedInsurance: Insurance = _selectedInsurance.value!!
        val selectedSourceAccount: Account = _selectedSourceAccount.value!!
        val selectedCurrency: PaymentCurrency = _selectedPaymentCurrency.value!!
        var additionalDetails: Map<String, String> = mapOf()
        var transferType: TransferType = TransferType.INTERBANK
        if (selectedCurrency.currency != selectedSourceAccount.currencyName) {
            additionalDetails = mapOf(
                "transactionType" to "intrabankCurrencyTransfer"
            )
            transferType = TransferType.INTRABANK
        }
        var destinationAccount: String = selectedInsurance.ibanBgn
        if (selectedCurrency.currency != "BGN") {
            destinationAccount = selectedInsurance.iban!!
        }
        return TransferSummary(
            selectedCurrency.amount.toDouble(),
            selectedCurrency.currency,
            0.00,
            selectedCurrency.currency,
            selectedSourceAccount,
            Payee(Beneficiary(selectedInsurance.insuranceAgencyName), destinationAccount),
            "BILL#${selectedInsurance.reason}",
            transferType,
            TransferExecution.NOW,
            additionalDetails,
            "POLICY#${selectedInsurance.policy}"
        )
    }

    fun chooseCurrencyPayment(currency: String, amount: String) {
        _selectedPaymentCurrency.postValue(PaymentCurrency(currency, amount))
    }

    fun clearCurrencyPayment() {
        _selectedPaymentCurrency.postValue(null)
    }

}