package tl.bnctl.banking.data.accounts

import androidx.lifecycle.MutableLiveData
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.accounts.model.AccountStatement

class AccountsRepository(private val dataSource: AccountsDataSource) {

    var accounts: List<Account>? = null
        private set // private setter with default implementation
    private var accountStatement: MutableLiveData<List<AccountStatement>>? = null

    init {
        accounts = null
    }

    suspend fun accountFetch(): Result<List<Account>> {
        val result = dataSource.accountFetch()
        if (result is Result.Success) {
            accounts = result.data
        }
        return result
    }

    suspend fun getDetailsForGivenAccount(accountId: String?): Result<Account> {
        return dataSource.getDetailsForGivenAccount(accountId)
    }

    suspend fun getAccountStatement(
        accountId: String,
        fromDate: String,
        toDate: String,
        limit: Int?
    ): Result<List<AccountStatement>> {
        val result = dataSource.getAccountStatementForAccount(accountId, fromDate, toDate, limit)
        if (result is Result.Success) {
            accountStatement = MutableLiveData(result.data)
        }
        return result
    }
}