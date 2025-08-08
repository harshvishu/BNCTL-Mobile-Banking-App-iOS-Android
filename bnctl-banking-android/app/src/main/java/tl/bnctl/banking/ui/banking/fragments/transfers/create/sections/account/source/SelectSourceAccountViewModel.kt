package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.AccountPermissions
import tl.bnctl.banking.data.accounts.AccountsRepository
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.services.PermissionService

class SelectSourceAccountViewModel(
    private val accountsRepository: AccountsRepository,
) : ViewModel() {

    var filterRequirement: ((Account) -> Boolean) = { true }

    // List of accounts that the current user has permissions on.
    // Use when selecting the source account for a transfer or any action with requirement accounts with permission
    private val _accountsWithPermission = MutableLiveData<Result<List<Account>>>().apply {}
    val accountsWithPermission: LiveData<Result<List<Account>>> = _accountsWithPermission

    // Full list of accounts for the current user.
    // Use when the permissions on the account are not relevant for the action.
    // Ex. destination account of a transfer
    private val _accountsFullList = MutableLiveData<Result<List<Account>>>().apply {}
    val accountsFullList: LiveData<Result<List<Account>>> = _accountsFullList

    private val _selectedAccount: MutableLiveData<Account?> by lazy { MutableLiveData<Account?>(null) }
    val selectedAccount: LiveData<Account?> = _selectedAccount

    private val _preselectAccount: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }
    val preselectAccount: LiveData<String?> = _preselectAccount

    private val _selectAccountError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val selectAccountError: LiveData<Int?> = _selectAccountError

    private val _loadingAccounts: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val loadingAccounts: LiveData<Boolean> = _loadingAccounts

    fun accountFetch(classParam: String) {
        _loadingAccounts.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = accountsRepository.accountFetch()
            _loadingAccounts.postValue(false)
            if (result is Result.Success) {
                val resultFilteredAccounts = Result.Success(
                    PermissionService.getInstance().filterAccountsAccordingToPermissions(
                        listOf(
                            AccountPermissions.INITIATE.permission
                        ),
                        result.data
                    )
                )
                _accountsWithPermission.postValue(resultFilteredAccounts)
                _accountsFullList.postValue(Result.Success((result.data)))
            }
        }
    }

    fun isSelectedAccountValid(): Boolean {
        return selectedAccount.value != null
    }

    fun selectAccount(account: Account?) {
        _selectedAccount.value = account
    }

    fun preselectAccount(accountNumber: String) {
        _preselectAccount.value = accountNumber
    }

    fun clearPreselectedAccount() {
        _preselectAccount.value = null
    }

    fun raiseSelectAccountError(errorId: Int?) {
        _selectAccountError.value = errorId
    }

    fun clearSelectAccountError() {
        _selectAccountError.value = null
    }
}
