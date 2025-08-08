package tl.bnctl.banking.ui.banking.fragments.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.accounts.AccountsDataSource
import tl.bnctl.banking.data.accounts.AccountsRepository
import tl.bnctl.banking.data.accounts.AccountsService
import tl.bnctl.banking.data.current_user.CurrentUserService

/**
 * ViewModel provider factory to instantiate AccountsViewModel.
 */
class AccountsViewModelFactory : ViewModelProvider.Factory {

    private fun getAccountsService(): AccountsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(AccountsService::class.java)
    }

    private fun getCurrentUserService(): CurrentUserService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(CurrentUserService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountsViewModel::class.java)) {
            val accountsService = getAccountsService()
            val currentUserService = getCurrentUserService()
            return AccountsViewModel(
                accountsRepository = AccountsRepository(
                    dataSource = AccountsDataSource(accountsService)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}