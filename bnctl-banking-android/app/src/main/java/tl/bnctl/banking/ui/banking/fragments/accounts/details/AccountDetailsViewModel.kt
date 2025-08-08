package tl.bnctl.banking.ui.banking.fragments.accounts.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.AccountsRepository
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.accounts.model.AccountStatement
import tl.bnctl.banking.ui.utils.DateUtils
import java.text.DateFormat
import java.util.*

class AccountDetailsViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _accountDetails = MutableLiveData<Result<Account>>()
    private val _accountStatement = MutableLiveData<Result<List<AccountStatement>>>()
    private val _isLoadingAccountDetails: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(
            false
        )
    }
    private val _isLoadingAccountStatements: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(
            false
        )
    }

    val isLoadingAccountDetails: LiveData<Boolean> = _isLoadingAccountDetails
    val isLoadingAccountStatements: LiveData<Boolean> = _isLoadingAccountStatements

    val accountDetails: LiveData<Result<Account>> = _accountDetails
    val accountStatement: LiveData<Result<List<AccountStatement>>> = _accountStatement

    lateinit var accountId: String

    fun getAccountDetails(accountId: String) {
        _isLoadingAccountDetails.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val accountDetailsResult = accountsRepository.getDetailsForGivenAccount(accountId)
            _accountDetails.postValue(accountDetailsResult)
            _isLoadingAccountDetails.postValue(false)
        }
    }

    fun getAccountStatement(
        accountId: String,
        fromDate: Date,
        toDate: Date,
        contextDateFormat: DateFormat?
    ) {
        _isLoadingAccountStatements.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val fromDateString =
                if (contextDateFormat != null) {
                    DateUtils.formatDate(contextDateFormat, fromDate)
                } else {
                    DateUtils.formatDateISO(fromDate)
                }
            val toDateString =
                if (contextDateFormat != null) {
                    DateUtils.formatDate(contextDateFormat, toDate)
                } else {
                    DateUtils.formatDateISO(toDate)
                }

            val accountStatementResult = accountsRepository.getAccountStatement(
                accountId,
                fromDateString,
                toDateString,
                if (BuildConfig.ACCOUNT_STATEMENT_LIMIT.isNotBlank()) BuildConfig.ACCOUNT_STATEMENT_LIMIT.toInt() else null
            )
            _accountStatement.postValue(accountStatementResult)
            _isLoadingAccountStatements.postValue(false)
        }
    }
}