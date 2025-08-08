package tl.bnctl.banking.ui.banking.fragments.cards.transaction_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.cards.CardsDataSource
import tl.bnctl.banking.data.cards.CardsRepository
import tl.bnctl.banking.data.cards.CardsService

class CardTransactionHistoryViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardTransactionHistoryViewModel::class.java)) {
            return CardTransactionHistoryViewModel(
                cardsRepository = CardsRepository(CardsDataSource(getCardsService()))
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getCardsService(): CardsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(CardsService::class.java)
    }
}