package tl.bnctl.banking.ui.banking.fragments.services.utilityBills.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.billpayments.BillPaymentService

class UtilityBillConfirmViewModelFactory : ViewModelProvider.Factory {

    private fun getBillPaymentService(): BillPaymentService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(BillPaymentService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UtilityBillConfirmViewModel::class.java)) {
            return UtilityBillConfirmViewModel(
                billPaymentService = getBillPaymentService()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

}