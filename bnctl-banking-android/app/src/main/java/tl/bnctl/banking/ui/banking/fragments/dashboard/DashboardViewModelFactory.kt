package tl.bnctl.banking.ui.banking.fragments.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.accounts.AccountsDataSource
import tl.bnctl.banking.data.accounts.AccountsRepository
import tl.bnctl.banking.data.accounts.AccountsService
import tl.bnctl.banking.data.cards.CardsDataSource
import tl.bnctl.banking.data.cards.CardsRepository
import tl.bnctl.banking.data.cards.CardsService

class DashboardViewModelFactory : ViewModelProvider.Factory {

    private fun getAccountsService(): AccountsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(AccountsService::class.java)
    }

    private fun getCardsService(): CardsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(CardsService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            val accountsService = getAccountsService()
            val cardsService = getCardsService()
            return DashboardViewModel(
                accountsRepository = AccountsRepository(
                    dataSource = AccountsDataSource(accountsService)
                ),
                cardsRepository = CardsRepository(
                    dataSource = CardsDataSource(cardsService)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}