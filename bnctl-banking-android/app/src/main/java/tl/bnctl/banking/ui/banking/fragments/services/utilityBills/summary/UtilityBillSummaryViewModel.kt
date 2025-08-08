package tl.bnctl.banking.ui.banking.fragments.services.utilityBills.summary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.billpayments.BillPaymentService
import tl.bnctl.banking.data.billpayments.model.BillPaymentValidationRequest
import tl.bnctl.banking.data.transfers.model.TransferValidate
import tl.bnctl.banking.data.transfers.model.TransferValidateResult
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class UtilityBillSummaryViewModel(
    private val billPaymentService: BillPaymentService
) : ViewModel() {

    private val _TAG = UtilityBillSummaryViewModel::class.simpleName

    private val _selectedBills: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>(
            listOf()
        )
    }
    val selectedBills: LiveData<List<String>> = _selectedBills

    private val _validationResult = MutableLiveData<TransferValidateResult>()
    val validationResult: LiveData<TransferValidateResult> = _validationResult

    fun startBillPaymentValidation(billPaymentValidationRequest: BillPaymentValidationRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            // Request from the repository to login.
            val result = validateBillPayment(billPaymentValidationRequest)
            if (result is Result.Success) {
                _validationResult.postValue(TransferValidateResult(success = result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _validationResult.postValue(TransferValidateResult(error = errorResult))
                Log.e(_TAG, errorResult.message)
            }
        }
    }

    private suspend fun validateBillPayment(billPaymentValidationRequest: BillPaymentValidationRequest): Result<TransferValidate> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            var validationResult: JsonObject
            try {
                billPaymentService.validateBillPayment(
                    accessToken, mapOf(
                        "paymentAmount" to billPaymentValidationRequest.paymentAmount,
                        "sourceAccount" to billPaymentValidationRequest.sourceAccount,
                        "sourceAccountId" to billPaymentValidationRequest.sourceAccountId,
                        "paymentCurrency" to billPaymentValidationRequest.paymentCurrency,
                        "executionDate" to billPaymentValidationRequest.executionDate,
                        "executionTime" to billPaymentValidationRequest.executionTime
                    )
                ).also { validationResult = it }
            } catch (e: Exception) {
                Log.e(
                    UtilityBillSummaryViewModel::class.java.canonicalName,
                    "Error validating the payment",
                    e
                )
                return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            }

            val billPaymentValidateResult =
                Gson().fromJson(
                    validationResult.get("result").toString(),
                    TransferValidate::class.java
                )
            return Result.Success(billPaymentValidateResult)
        } catch (e: Throwable) {
            return Result.Error("Error validating the transfer: ${e.localizedMessage}")
        }
    }

    fun setSelectedBills(bills: List<String>) {
        _selectedBills.value = bills
    }
}