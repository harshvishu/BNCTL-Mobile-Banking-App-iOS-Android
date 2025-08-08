package tl.bnctl.banking.ui.banking.fragments.transfers.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.transfers.TransferDataSource
import tl.bnctl.banking.data.transfers.TransferRepository
import tl.bnctl.banking.data.transfers.TransferService

class TransferHistoryViewModelFactory : ViewModelProvider.Factory {

    private fun getTransferService(): TransferService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(TransferService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransferHistoryViewModel::class.java)) {
            return TransferHistoryViewModel(
                TransferRepository(TransferDataSource(getTransferService()))
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}