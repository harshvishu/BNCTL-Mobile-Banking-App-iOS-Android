package tl.bnctl.banking.ui.banking.fragments.transfers.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.transfers.TransferDataSource
import tl.bnctl.banking.data.transfers.TransferRepository
import tl.bnctl.banking.data.transfers.TransferService

class TransferSummaryViewModelFactory : ViewModelProvider.Factory {

    private fun getTransferService(): TransferService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(TransferService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransferSummaryViewModel::class.java)) {
            return TransferSummaryViewModel(
                TransferRepository(
                    dataSource = TransferDataSource(getTransferService())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}