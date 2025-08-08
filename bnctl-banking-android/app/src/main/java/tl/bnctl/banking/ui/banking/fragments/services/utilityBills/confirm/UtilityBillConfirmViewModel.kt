package tl.bnctl.banking.ui.banking.fragments.services.utilityBills.confirm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.billpayments.BillPaymentService
import tl.bnctl.banking.data.billpayments.model.BillPaymentApproveNumerousRequest
import tl.bnctl.banking.data.fallback.FallbackRequestParams
import tl.bnctl.banking.data.transfers.model.TransferConfirm
import tl.bnctl.banking.data.transfers.model.TransferConfirmResult
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class UtilityBillConfirmViewModel(
    private val billPaymentService: BillPaymentService
) : ViewModel() {

    private val _TAG = UtilityBillConfirmViewModel::class.simpleName

    private val _confirmBillPaymentResult = MutableLiveData<TransferConfirmResult>()
    val confirmBillPaymentResult: LiveData<TransferConfirmResult> = _confirmBillPaymentResult

    // Used for telling the fallback confirm screen that PIN is required
    private val _usePin = MutableLiveData<Boolean>().apply { value = false }
    val usePin: LiveData<Boolean> = _usePin

    fun startBillPaymentConfirmation(
        billPaymentApproveNumerousRequest: BillPaymentApproveNumerousRequest,
        fallbackParams: FallbackRequestParams?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // Request from the repository to login.
            val result = confirmBillPayment(billPaymentApproveNumerousRequest, fallbackParams)
            if (result is Result.Success) {
                _confirmBillPaymentResult.postValue(TransferConfirmResult(success = result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _confirmBillPaymentResult.postValue(TransferConfirmResult(error = errorResult))
                Log.e(_TAG, errorResult.message)
            }
        }
    }

    private suspend fun confirmBillPayment(
        billPaymentApproveNumerousRequest: BillPaymentApproveNumerousRequest,
        fallbackParams: FallbackRequestParams?
    ): Result<TransferConfirm> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            var confirmationResult: JsonObject
            try {
                val body = mutableMapOf(
                    "billPayments" to billPaymentApproveNumerousRequest.billPayments,
                    "sourceAccount" to billPaymentApproveNumerousRequest.sourceAccount,
                    "sourceAccountId" to billPaymentApproveNumerousRequest.sourceAccountId,
                    "sourceAccountHolder" to billPaymentApproveNumerousRequest.sourceAccountHolder,
                )

                if (fallbackParams != null) {
                    body["fallbackParams"] = fallbackParams
                }

                billPaymentService.approveBillPayment(accessToken, body)
                    .also { confirmationResult = it }
            } catch (e: Exception) {
                Log.e(
                    UtilityBillConfirmViewModel::class.java.canonicalName,
                    "Error Confirming the payment",
                    e
                )
                if (e is HttpException) {
                    return Result.createError(e)
                }
                return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            }

            val transferConfirmResult =
                Gson().fromJson(
                    confirmationResult.get("result").toString(),
                    TransferConfirm::class.java
                )
            return Result.Success(transferConfirmResult)
        } catch (e: Throwable) {
            return Result.Error("Error Confirming the payment: ${e.localizedMessage}")
        }
    }

    fun setUsePin(usePin: Boolean) {
        _usePin.postValue(usePin)
    }

}