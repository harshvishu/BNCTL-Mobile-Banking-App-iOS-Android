package tl.bnctl.banking.ui.banking.fragments.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.cards.CardsDataSource
import tl.bnctl.banking.data.cards.CardsRepository
import tl.bnctl.banking.data.cards.CardsService
import tl.bnctl.banking.data.current_user.CurrentUserService

/**
 * ViewModel provider factory to instantiate CardsViewModel.
 */
class CardsViewModelFactory : ViewModelProvider.Factory {

    private fun getCardsService(): CardsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(CardsService::class.java)
    }

    private fun getCurrentUserService(): CurrentUserService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(CurrentUserService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
            val cardsService = getCardsService()
            val currentUserService = getCurrentUserService()
            return CardsViewModel(
                cardsRepository = CardsRepository(
                    dataSource = CardsDataSource(cardsService)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}