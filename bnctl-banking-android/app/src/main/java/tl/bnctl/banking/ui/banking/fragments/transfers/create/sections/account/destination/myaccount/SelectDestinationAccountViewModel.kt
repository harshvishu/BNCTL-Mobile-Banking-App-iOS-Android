package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.destination.myaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.AccountsRepository
import tl.bnctl.banking.data.accounts.model.Account

class SelectDestinationAccountViewModel(
    private val accountsRepository: AccountsRepository,
) : ViewModel() {

    private val _selectedDestinationAccount: MutableLiveData<Account?> by lazy { MutableLiveData<Account?>() }
    val selectedDestinationAccount: LiveData<Account?> = _selectedDestinationAccount

    private val _preselectedDestinationAccount: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }
    val preselectedDestinationAccount: LiveData<String?> = _preselectedDestinationAccount

    private val _accountsWithPermission = MutableLiveData<Result<List<Account>>>().apply {}
    val accountsWithPermission: LiveData<Result<List<Account>>> = _accountsWithPermission

    private val _accountsFullList = MutableLiveData<Result<List<Account>>>().apply {}
    val accountsFullList: LiveData<Result<List<Account>>> = _accountsFullList

    private val _selectAccountError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val selectAccountError: LiveData<Int?> = _selectAccountError

    fun accountFetch(classParam: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = accountsRepository.accountFetch()
            if (result is Result.Success) {
                val resultFilteredAccounts = Result.Success(result.data)
                _accountsWithPermission.postValue(resultFilteredAccounts)
                _accountsFullList.postValue(Result.Success((result.data)))
            }
        }
    }

    fun selectDestinationAccount(account: Account?) {
        _selectedDestinationAccount.value = account
    }

    fun isSelectedAccountValid(): Boolean {
        return selectedDestinationAccount.value != null
    }

    fun preselectDestinationAccount(accountNumber: String) {
        _preselectedDestinationAccount.value = accountNumber
    }

    fun clearPreselectedDestinationAccount() {
        _preselectedDestinationAccount.value = null
    }


    fun raiseSelectAccountError(errorId: Int?) {
        _selectAccountError.value = errorId
    }

    fun clearSelectAccountError() {
        _selectAccountError.value = null
    }
}