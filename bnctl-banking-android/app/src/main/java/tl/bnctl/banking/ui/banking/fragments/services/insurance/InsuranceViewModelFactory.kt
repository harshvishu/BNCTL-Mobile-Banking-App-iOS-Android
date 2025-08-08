package tl.bnctl.banking.ui.banking.fragments.services.insurance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.insurance.InsuranceDataSource
import tl.bnctl.banking.data.insurance.InsuranceRepository
import tl.bnctl.banking.data.insurance.InsuranceService
import tl.bnctl.banking.data.transfers.TransferDataSource
import tl.bnctl.banking.data.transfers.TransferRepository
import tl.bnctl.banking.data.transfers.TransferService

class InsuranceViewModelFactory() : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InsuranceViewModel::class.java)) {
            return InsuranceViewModel(
                insuranceRepository = InsuranceRepository(
                    dataSource = InsuranceDataSource(getInsuranceService())
                ),
                transferRepository = TransferRepository(
                    dataSource = TransferDataSource(getTransferService())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getInsuranceService(): InsuranceService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(InsuranceService::class.java)
    }

    private fun getTransferService(): TransferService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(TransferService::class.java)
    }
}
