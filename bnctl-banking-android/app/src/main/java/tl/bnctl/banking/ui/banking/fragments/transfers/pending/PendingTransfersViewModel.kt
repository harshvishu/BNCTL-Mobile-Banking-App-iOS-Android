package tl.bnctl.banking.ui.banking.fragments.transfers.pending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.fallback.FallbackRequestParams
import tl.bnctl.banking.data.transfers.model.PendingTransfer
import tl.bnctl.banking.data.transfers.model.PendingTransferConfirmOrRejectResult
import tl.bnctl.banking.data.transfers.pending.PendingTransferRepository

class PendingTransfersViewModel(
    private val pendingTransferRepository: PendingTransferRepository
) : ViewModel() {

    private val _pendingTransfers = MutableLiveData<Result<List<PendingTransfer>>?>()
    val pendingTransfers: LiveData<Result<List<PendingTransfer>>?> = _pendingTransfers

    private val _numSelectedPendingTransfers = MutableLiveData(0)
    val numSelectedPendingTransfers: LiveData<Int> = _numSelectedPendingTransfers

    private val _confirmationRequestResult =
        MutableLiveData<Result<PendingTransferConfirmOrRejectResult>>()
    val confirmationRequestResult: LiveData<Result<PendingTransferConfirmOrRejectResult>> =
        _confirmationRequestResult

    private val _rejectionRequestResult =
        MutableLiveData<Result<PendingTransferConfirmOrRejectResult>>()
    val rejectionRequestResult: LiveData<Result<PendingTransferConfirmOrRejectResult>> =
        _rejectionRequestResult

    // Used for telling the fallback confirm screen that PIN is required
    private val _usePin = MutableLiveData<Boolean>().apply { value = false }
    val usePin: LiveData<Boolean> = _usePin

    fun fetchPendingTransfers() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = pendingTransferRepository.fetchPendingTransfers()
            _pendingTransfers.postValue(result)
        }
    }

    fun rejectSelectedPendingTransfers() {
        return rejectSelectedPendingTransfers(null)
    }

    fun rejectSelectedPendingTransfers(fallbackParams: FallbackRequestParams?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_pendingTransfers.value is Result.Success) {
                _rejectionRequestResult.postValue(
                    pendingTransferRepository.rejectSelectedPendingTransfers(
                        retrieveIdsOfSelectedTransfers(),
                        fallbackParams
                    )
                )
            }
        }
    }

    fun confirmSelectedPendingTransfers() {
        confirmSelectedPendingTransfers(null)
    }

    fun confirmSelectedPendingTransfers(fallbackParams: FallbackRequestParams?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_pendingTransfers.value is Result.Success) {
                _confirmationRequestResult.postValue(
                    pendingTransferRepository.confirmSelectedPendingTransfers(
                        retrieveIdsOfSelectedTransfers(),
                        fallbackParams
                    )
                )
            }
        }
    }

    private fun retrieveIdsOfSelectedTransfers(): List<String> {
        return (_pendingTransfers.value as Result.Success<List<PendingTransfer>>)
            .data.filter { it.isSelected }
            .map { it.transferId }
    }

    fun selectAllPendingTransfers() {
        changeSelectStateOfAllPendingTransfers(true)
    }

    fun unselectAllPendingTransfers() {
        changeSelectStateOfAllPendingTransfers(false)
    }

    fun changeNumberOfSelectedPendingTransfers(isSelected: Boolean) {
        if (isSelected) _numSelectedPendingTransfers.postValue(numSelectedPendingTransfers.value!!.inc())
        else _numSelectedPendingTransfers.postValue(numSelectedPendingTransfers.value!!.dec())
    }

    private fun changeSelectStateOfAllPendingTransfers(state: Boolean) {
        if (pendingTransfers.value is Result.Success) {
            val pendingTransfers: List<PendingTransfer> =
                (pendingTransfers.value as Result.Success<List<PendingTransfer>>).data
            repeat(pendingTransfers.size) {
                pendingTransfers[it].isSelected = state
            }
            _numSelectedPendingTransfers.postValue(if (state) pendingTransfers.size else 0)
            _pendingTransfers.postValue(Result.Success(pendingTransfers))
        }
    }

    fun setUsePin(usePin: Boolean) {
        _usePin.postValue(usePin)
    }

}