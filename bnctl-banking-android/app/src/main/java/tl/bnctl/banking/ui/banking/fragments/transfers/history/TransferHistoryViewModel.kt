package tl.bnctl.banking.ui.banking.fragments.transfers.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.transfers.TransferRepository
import tl.bnctl.banking.data.transfers.model.TransferHistoryResult
import tl.bnctl.banking.ui.banking.fragments.statements.adapter.StatementsFilterData

class TransferHistoryViewModel(
    private val transferRepository: TransferRepository
) : ViewModel() {

    private val _transferHistory = MutableLiveData<Result<TransferHistoryResult>>().apply {}
    private val _isLoadingTransferHistory: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(
            false
        )
    }

    private val _transferHistoryPage = MutableLiveData<Result<TransferHistoryResult>>().apply {}

    val transferHistory: LiveData<Result<TransferHistoryResult>> = _transferHistory
    val transferHistoryPage: LiveData<Result<TransferHistoryResult>> = _transferHistoryPage
    val isLoadingTransferHistory: LiveData<Boolean> = _isLoadingTransferHistory

    fun fetchTransferHistory(filterData: StatementsFilterData, fetchPage: Boolean = false) {
        _isLoadingTransferHistory.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = transferRepository.fetchTransfers(
                filterData.startDate,
                filterData.endDate,
                filterData.pageNumber,
                filterData.pageSize
            )
            if (fetchPage) {
                _transferHistoryPage.postValue(result)
            } else {
                _transferHistory.postValue(result)
            }
            _isLoadingTransferHistory.postValue(false)
        }
    }
}