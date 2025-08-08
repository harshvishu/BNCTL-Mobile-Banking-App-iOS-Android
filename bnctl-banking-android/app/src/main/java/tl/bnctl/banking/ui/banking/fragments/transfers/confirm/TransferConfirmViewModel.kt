package tl.bnctl.banking.ui.banking.fragments.transfers.confirm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.transfers.TransferRepository
import tl.bnctl.banking.data.transfers.model.TransferConfirmRequest
import tl.bnctl.banking.data.transfers.model.TransferConfirmResult
import tl.bnctl.banking.data.transfers.model.TransferSummary

class TransferConfirmViewModel(
    private val transferRepository: TransferRepository
) : ViewModel() {

    private val _TAG = TransferConfirmViewModel::class.simpleName

    private val _confirmTransferResult = MutableLiveData<TransferConfirmResult>()
    val confirmTransferResult: LiveData<TransferConfirmResult> = _confirmTransferResult

    // Used for telling the fallback confirm screen that PIN is required
    private val _usePin = MutableLiveData<Boolean>().apply { value = false }
    val usePin: LiveData<Boolean> = _usePin

    fun startTransferConfirmation(transferConfirmation: TransferConfirmRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            // Request from the repository to login.
            val result = transferRepository.confirmTransfer(transferConfirmation)
            if (result is Result.Success) {
                _confirmTransferResult.postValue(TransferConfirmResult(success = result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _confirmTransferResult.postValue(TransferConfirmResult(error = errorResult))
                Log.e(_TAG, errorResult.message)
            }
        }
    }

    fun startTransferExecution(transferSummary: TransferSummary) {
        viewModelScope.launch(Dispatchers.IO) {
            // Request from the repository to login.
            val result = transferRepository.createAndExecuteTransfer(transferSummary)
            if (result is Result.Success) {
                _confirmTransferResult.postValue(TransferConfirmResult(success = result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _confirmTransferResult.postValue(TransferConfirmResult(error = errorResult))
                Log.e(_TAG, errorResult.message)
            }
        }
    }

    fun setUsePin(usePin: Boolean) {
        _usePin.postValue(usePin)
    }

}