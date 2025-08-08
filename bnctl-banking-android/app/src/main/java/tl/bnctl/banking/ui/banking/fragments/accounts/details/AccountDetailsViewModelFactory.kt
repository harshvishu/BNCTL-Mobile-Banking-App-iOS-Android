package tl.bnctl.banking.ui.banking.fragments.accounts.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.accounts.AccountsDataSource
import tl.bnctl.banking.data.accounts.AccountsRepository
import tl.bnctl.banking.data.accounts.AccountsService

class AccountDetailsViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountDetailsViewModel::class.java)) {
            val accountsService = getAccountsService()
            return AccountDetailsViewModel(
                accountsRepository = AccountsRepository(
                    dataSource = AccountsDataSource(accountsService)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.");
    }

    private fun getAccountsService(): AccountsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(AccountsService::class.java)
    }
}