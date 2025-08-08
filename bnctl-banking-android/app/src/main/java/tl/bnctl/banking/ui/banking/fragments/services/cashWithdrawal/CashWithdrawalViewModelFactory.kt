package tl.bnctl.banking.ui.banking.fragments.services.cashWithdrawal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.branches.BranchDataSource
import tl.bnctl.banking.data.branches.BranchRepository
import tl.bnctl.banking.data.branches.BranchService
import tl.bnctl.banking.data.cashWithdrawal.CashWithdrawalDataSource
import tl.bnctl.banking.data.cashWithdrawal.CashWithdrawalService

class CashWithdrawalViewModelFactory : ViewModelProvider.Factory {

    private fun getBranchesService(): BranchService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(BranchService::class.java)
    }

    private fun getCashWithdrawalService(): CashWithdrawalService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(CashWithdrawalService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CashWithdrawalViewModel::class.java)) {
            return CashWithdrawalViewModel(
                branchesRepository = BranchRepository(
                    dataSource = BranchDataSource(
                        branchService = getBranchesService()
                    )
                ),
                cashWithdrawalDataSource = CashWithdrawalDataSource(
                    cashWithdrawalService = getCashWithdrawalService()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}