package tl.bnctl.banking.ui.banking.fragments.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.AccountsRepository
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.cards.CardsRepository
import tl.bnctl.banking.data.cards.model.Card

class DashboardViewModel(
    private val accountsRepository: AccountsRepository,
    private val cardsRepository: CardsRepository
) : ViewModel() {

    private val _accounts = MutableLiveData<Result<List<Account>>>().apply {}
    private val _isLoading = MutableLiveData<Boolean>().apply {}
    private val _cards = MutableLiveData<Result<List<Card>>>().apply {}

    val accounts: LiveData<Result<List<Account>>> = _accounts
    val isLoading: LiveData<Boolean> = _isLoading
    val cards: LiveData<Result<List<Card>>> = _cards

    fun accountFetch() {
        _isLoading.value = true
        Log.d(
            TAG,
            "accountFetch: starting to fetch accounts with this data available: ${_accounts.value.toString()}"
        )
        viewModelScope.launch(Dispatchers.IO) {
            // fetch all accounts
            val result = accountsRepository.accountFetch()
            Log.d(TAG, "accountFetch: after request to backend: ${_accounts.value.toString()}")
            _isLoading.postValue(false)
            _accounts.postValue(result)
        }
    }

    fun cardFetch() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = cardsRepository.cardFetch()
            if (result != null) {
                _cards.postValue(result)
            }
        }
    }

    fun setAccountsResult(result: Result<List<Account>>) {
        _accounts.postValue(result)
    }

    companion object {
        val TAG: String = DashboardViewModel::class.java.simpleName
    }
}