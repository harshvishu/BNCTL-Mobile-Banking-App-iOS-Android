package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.accounts.AccountsDataSource
import tl.bnctl.banking.data.accounts.AccountsRepository
import tl.bnctl.banking.data.accounts.AccountsService

class SelectAccountViewModelFactory : ViewModelProvider.Factory {

    private fun getAccountsService(): AccountsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(AccountsService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelectSourceAccountViewModel::class.java)) {
            val accountsService = getAccountsService()
            return SelectSourceAccountViewModel(
                accountsRepository = AccountsRepository(
                    dataSource = AccountsDataSource(accountsService)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}