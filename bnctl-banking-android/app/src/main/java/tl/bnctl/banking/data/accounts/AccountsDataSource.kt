package tl.bnctl.banking.data.accounts

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.accounts.model.AccountStatement
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class AccountsDataSource(private val accountsService: AccountsService) {

    val gson: Gson = Gson()

    suspend fun accountFetch(): Result<List<Account>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error(
                    "Session expired",
                    Constants.SESSION_EXPIRED_CODE,
                    "sessionExpired"
                )
            val accountsResult: JsonArray
            accountsService.accounts(accessToken)
                .also { accountsResult = it.asJsonObject.get("result").asJsonArray }
            val accountList: List<Account> = accountsResult.map { accountJsonObject ->
                gson.fromJson(accountJsonObject.toString(), Account::class.java)
            }
            return Result.Success(accountList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx, "accounts")
        } catch (e: Exception) {
            return Result.createError(e)
        }
    }

    suspend fun getDetailsForGivenAccount(accountId: String?): Result<Account> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error(
                    "Session expired",
                    Constants.SESSION_EXPIRED_CODE,
                    "sessionExpired"
                )
            lateinit var accountDetailsResult: JsonObject
            accountId?.let {
                accountsService.accountDetails(accessToken, it)
                    .also { accountDetailsResult = it }
            }
            val gson = Gson()
            val accountDetails =
                gson.fromJson(accountDetailsResult.get("result").toString(), Account::class.java)
            return Result.Success(accountDetails)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx, "accounts")
        } catch (e: Exception) {
            return Result.createError(e);
        }
    }

    suspend fun getAccountStatementForAccount(
        accountId: String,
        fromDate: String,
        toDate: String,
        limit: Int?
    ): Result<List<AccountStatement>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error(
                    "Session expired",
                    Constants.SESSION_EXPIRED_CODE,
                    "sessionExpired"
                )
            lateinit var accountStatementResult: JsonObject
            accountsService.accountStatement(accessToken, accountId, fromDate, toDate, limit)
                .also { accountStatementResult = it.asJsonObject.get("result").asJsonObject }
            val gson = Gson()
            val accountStatementList = ArrayList<AccountStatement>()

            val accountStatements: JsonArray = accountStatementResult.get("records") as JsonArray
            for (accountJsonObject in accountStatements) {
                val account = gson.fromJson(accountJsonObject.toString(), AccountStatement::class.java)
                accountStatementList.add(account)
            }
            return Result.Success(accountStatementList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (e: Exception) {
            return Result.createError(e);
        }
    }

}