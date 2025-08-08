package tl.bnctl.banking.ui.banking.fragments.transfers.create.validation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.eod.EoDDataSource
import tl.bnctl.banking.data.eod.EoDRepository
import tl.bnctl.banking.data.eod.EoDService
import tl.bnctl.banking.data.templates.TemplatesDataSource
import tl.bnctl.banking.data.templates.TemplatesRepository
import tl.bnctl.banking.data.templates.TemplatesService
import tl.bnctl.banking.data.transfers.TransferService

class TransferValidateViewModelFactory : ViewModelProvider.Factory {

    private fun getTransferService(): TransferService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(TransferService::class.java)
    }

    private fun getEoDService(): EoDService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(EoDService::class.java)
    }

    private fun getTemplatesService(): TemplatesService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(TemplatesService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransferValidateViewModel::class.java)) {
            return TransferValidateViewModel(
                getTransferService(),
                EoDRepository(EoDDataSource(getEoDService())),
                TemplatesRepository(
                    dataSource = TemplatesDataSource(getTemplatesService())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}