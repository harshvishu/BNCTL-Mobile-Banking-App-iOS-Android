package tl.bnctl.banking.ui.banking.fragments.transfers.pending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.transfers.pending.PendingTransferDataSource
import tl.bnctl.banking.data.transfers.pending.PendingTransferRepository
import tl.bnctl.banking.data.transfers.pending.PendingTransferService

class PendingTransfersViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PendingTransfersViewModel::class.java)) {
            return PendingTransfersViewModel(
                pendingTransferRepository = PendingTransferRepository(
                    dataSource = PendingTransferDataSource(getPendingTransferService())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getPendingTransferService(): PendingTransferService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(PendingTransferService::class.java)
    }
}