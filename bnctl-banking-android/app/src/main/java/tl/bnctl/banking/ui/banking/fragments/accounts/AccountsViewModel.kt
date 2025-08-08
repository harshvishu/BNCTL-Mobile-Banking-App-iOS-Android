package tl.bnctl.banking.ui.banking.fragments.accounts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.AccountsRepository
import tl.bnctl.banking.data.accounts.model.Account

class AccountsViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _accounts = MutableLiveData<Result<List<Account>>>().apply {}
    val accounts: LiveData<Result<List<Account>>> = _accounts

    fun getAccounts(transactionType: String?, isDebit: Boolean?) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = accountsRepository.accountFetch()
            if (result != null) {
                _accounts.postValue(result)
            }
        }
    }
}