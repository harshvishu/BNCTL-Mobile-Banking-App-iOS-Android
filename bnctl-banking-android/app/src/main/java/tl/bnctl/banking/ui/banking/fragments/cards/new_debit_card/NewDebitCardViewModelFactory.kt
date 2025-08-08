package tl.bnctl.banking.ui.banking.fragments.cards.new_debit_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.branches.BranchDataSource
import tl.bnctl.banking.data.branches.BranchRepository
import tl.bnctl.banking.data.branches.BranchService
import tl.bnctl.banking.data.cards.CardsDataSource
import tl.bnctl.banking.data.cards.CardsRepository
import tl.bnctl.banking.data.cards.CardsService

class NewDebitCardViewModelFactory : ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewDebitCardViewModel::class.java)) {
            val dataSource = CardsDataSource(getCardsService())
            return NewDebitCardViewModel(
                branchRepository = BranchRepository(
                    dataSource = BranchDataSource(getBranchService())
                ),
                cardsDataSource = dataSource,
                cardsRepository = CardsRepository(
                    dataSource = dataSource
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getBranchService(): BranchService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(BranchService::class.java)
    }

    private fun getCardsService(): CardsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(CardsService::class.java)
    }

}