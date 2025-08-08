package tl.bnctl.banking.ui.banking.fragments.services.utilityBills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.billers.BillerDataSource
import tl.bnctl.banking.data.billers.BillerRepository
import tl.bnctl.banking.data.billers.BillerService
import tl.bnctl.banking.data.eod.EoDDataSource
import tl.bnctl.banking.data.eod.EoDRepository
import tl.bnctl.banking.data.eod.EoDService

class UtilityBillViewModelFactory : ViewModelProvider.Factory {

    private fun getBillerService(): BillerService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(BillerService::class.java)
    }

    private fun getEoDService(): EoDService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(EoDService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UtilityBillViewModel::class.java)) {
            val billerService = getBillerService()
            val eodService = getEoDService()
            return UtilityBillViewModel(
                billerRepository = BillerRepository(BillerDataSource(billerService)),
                eoDRepository = EoDRepository(EoDDataSource(eodService))
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}