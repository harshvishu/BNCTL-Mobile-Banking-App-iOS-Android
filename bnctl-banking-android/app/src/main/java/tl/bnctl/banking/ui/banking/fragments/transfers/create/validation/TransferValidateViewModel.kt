package tl.bnctl.banking.ui.banking.fragments.transfers.create.validation

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
import tl.bnctl.banking.data.eod.EoDRepository
import tl.bnctl.banking.data.eod.model.EoDResult
import tl.bnctl.banking.data.templates.TemplatesRepository
import tl.bnctl.banking.data.templates.model.Bank
import tl.bnctl.banking.data.transfers.TransferService
import tl.bnctl.banking.data.transfers.model.Transfer
import tl.bnctl.banking.data.transfers.model.TransferValidate
import tl.bnctl.banking.data.transfers.model.TransferValidateResult
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class TransferValidateViewModel(
    private val transferService: TransferService,
    private val eoDRepository: EoDRepository,
    private val templatesRepository: TemplatesRepository
) : ViewModel() {

    private val _TAG = TransferValidateViewModel::class.simpleName

    private val _validationResult = MutableLiveData<TransferValidateResult>()
    val validationResult: LiveData<TransferValidateResult> = _validationResult

    private val _pendingTransferResult = MutableLiveData<Result<Transfer>>()
    val pendingTransferResult: LiveData<Result<Transfer>> = _pendingTransferResult

    private val _eodResult = MutableLiveData<EoDResult>()
    val eodResult: LiveData<EoDResult> = _eodResult

    private val _banksResult = MutableLiveData<Result<List<Bank>>>().apply {}
    val banksResult: LiveData<Result<List<Bank>>> = _banksResult

    fun startTransferValidation(transfer: Transfer) {
        viewModelScope.launch(Dispatchers.IO) {
            // Request from the repository to login.
            val result = validateTransfer(transfer)
            if (result is Result.Success) {
                _validationResult.postValue(TransferValidateResult(success = result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _validationResult.postValue(TransferValidateResult(error = errorResult))
                Log.e(_TAG, errorResult.message)
            }
        }
    }

    fun createPendingTransfer(transfer: Transfer) {
        viewModelScope.launch(Dispatchers.IO) {
            // Request from the repository to login.
            val result = sendPendingTransferRequest(transfer)
            _pendingTransferResult.postValue(result)
        }
    }

    private suspend fun validateTransfer(transfer: Transfer): Result<TransferValidate> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            var validationResult: JsonObject
            try {
                val request = mutableMapOf(
                    "amount" to transfer.amount,
                    "description" to transfer.description,
                    "sourceAccount" to transfer.sourceAccount,
                    "sourceAccountId" to transfer.sourceAccountId,
                    "sourceAccountCurrency" to transfer.sourceAccountCurrency,
                    "destinationAccount" to transfer.destinationAccount,
                    "destinationAccountCurrency" to transfer.destinationAccountCurrency,
                    "recipientName" to transfer.destinationAccountHolder,
                    "transferType" to transfer.transferType,
                    "executionType" to transfer.executionType,
                    "additionalDetails" to transfer.additionalDetails
                )
                if (transfer.newPayee != null) {
                    request["newPayee"] = transfer.newPayee!!
                }
                transferService.validateTransfer(
                    accessToken, request
                ).also { validationResult = it }
            } catch (e: Exception) {
                Log.e(
                    TransferValidateViewModel::class.java.canonicalName,
                    "Error validating the transfer",
                    e
                )
                return Result.createError(e)
            }

            val transferValidateResult =
                Gson().fromJson(
                    validationResult.get("result").toString(),
                    TransferValidate::class.java
                )
            return Result.Success(transferValidateResult)
        } catch (e: Throwable) {
            return Result.Error("Error validating the transfer: ${e.localizedMessage}")
        }
    }

    private suspend fun sendPendingTransferRequest(transfer: Transfer): Result<Transfer> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            var pendingTransferResult: JsonObject
            try {
                val request = mutableMapOf(
                    "amount" to transfer.amount,
                    "description" to transfer.description,
                    "sourceAccount" to transfer.sourceAccount,
                    "sourceAccountId" to transfer.sourceAccountId,
                    "sourceAccountCurrency" to transfer.sourceAccountCurrency,
                    "destinationAccount" to transfer.destinationAccount,
                    "destinationAccountCurrency" to transfer.destinationAccountCurrency,
                    "recipientName" to transfer.destinationAccountHolder,
                    "transferType" to transfer.transferType,
                    "executionType" to transfer.executionType,
                    "additionalDetails" to transfer.additionalDetails
                )
                if (transfer.newPayee != null) {
                    request["newPayee"] = transfer.newPayee!!
                }
                transferService.createPendingTransfer(
                    accessToken, request
                ).also { pendingTransferResult = it }
            } catch (e: Exception) {
                Log.e(
                    TransferValidateViewModel::class.java.canonicalName,
                    "Error Creating pending transfer",
                    e
                )
                return Result.createError(e)
            }

            val pendingTransfer =
                Gson().fromJson(
                    pendingTransferResult.get("result").toString(),
                    Transfer::class.java
                )
            return Result.Success(pendingTransfer)
        } catch (e: Throwable) {
            return Result.Error("Error creating pending transfer: ${e.localizedMessage}")
        }
    }

    fun clearValidationResult() {
        _validationResult.value = TransferValidateResult()
    }

    fun checkEoD() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = eoDRepository.checkEoD()
            if (result is Result.Success) {
                _eodResult.postValue(EoDResult(true, result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _eodResult.postValue(EoDResult(error = errorResult))
                Log.e(_TAG, errorResult.message)
            }
        }
    }

    fun fetchBanks() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = templatesRepository.fetchBanks()
            _banksResult.postValue(result)
        }
    }
}