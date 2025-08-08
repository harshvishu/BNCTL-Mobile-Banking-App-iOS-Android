package tl.bnctl.banking.ui.banking.fragments.cards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.cards.CardsRepository
import tl.bnctl.banking.data.cards.model.Card

class CardsViewModel(
    private val cardsRepository: CardsRepository
) : ViewModel() {

    private val _cards = MutableLiveData<Result<List<Card>>>().apply {}
    val cards: LiveData<Result<List<Card>>> = _cards

    fun fetchCards() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = cardsRepository.cardFetch()
            _cards.postValue(result)
        }
    }

    fun getCards(): List<Card> {
        if (cards.value is Result.Success) {
            return (cards.value as Result.Success).data
        }
        return emptyList()
    }
}