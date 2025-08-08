package tl.bnctl.banking.ui.banking.fragments.cards.credit_card_statement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.cards.CardsDataSource
import tl.bnctl.banking.data.cards.CardsRepository
import tl.bnctl.banking.data.cards.CardsService

class CreditCardStatementViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreditCardStatementViewModel::class.java)) {
            return CreditCardStatementViewModel(
                CardsRepository(
                    CardsDataSource(getCardsService())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.");
    }

    private fun getCardsService(): CardsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(CardsService::class.java)
    }
}