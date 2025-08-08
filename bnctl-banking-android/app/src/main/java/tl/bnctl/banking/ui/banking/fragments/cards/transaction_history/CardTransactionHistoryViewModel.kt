package tl.bnctl.banking.ui.banking.fragments.cards.transaction_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.StatementsDataHolder
import tl.bnctl.banking.data.cards.CardsRepository

class CardTransactionHistoryViewModel(
    private val cardsRepository: CardsRepository,
) : ViewModel() {

    private val _cardStatement = MutableLiveData<Result<List<StatementsDataHolder>>>()
    private val _isLoadingCardStatements: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false)}

    val cardStatement: LiveData<Result<List<StatementsDataHolder>>> = _cardStatement
    val isLoadingCardStatements: LiveData<Boolean> = _isLoadingCardStatements

    fun fetchCardStatement(
        cardNumber: String,
        fromDate: String,
        toDate: String
    ) {
        _isLoadingCardStatements.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = cardsRepository.fetchCardStatement(cardNumber, fromDate, toDate)
            _cardStatement.postValue(result)
            _isLoadingCardStatements.postValue(false)
        }
    }

}