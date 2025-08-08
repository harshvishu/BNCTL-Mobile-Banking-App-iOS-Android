package tl.bnctl.banking.data.cashWithdrawal

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.cashWithdrawal.model.CashWithdrawal
import tl.bnctl.banking.data.cashWithdrawal.model.CashWithdrawalRequest
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class CashWithdrawalDataSource(
    private val cashWithdrawalService: CashWithdrawalService
) {

    val gson: Gson = Gson()

    suspend fun withdrawCash(cashWithdrawalRequest: CashWithdrawalRequest): Result<CashWithdrawal> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            var cashWithdrawal: JsonObject
            cashWithdrawalService.requestCashWithdrawal(
                accessToken, mapOf(
                    "amount" to cashWithdrawalRequest.amount,
                    "description" to cashWithdrawalRequest.description,
                    "currency" to cashWithdrawalRequest.currency,
                    "executionDate" to cashWithdrawalRequest.executionDate,
                    "branch" to cashWithdrawalRequest.branch
                )
            ).also { cashWithdrawal = it }
            val cashWithdrawalRequestValidateResult =
                gson.fromJson(cashWithdrawal.toString(), CashWithdrawal::class.java)
            return Result.Success(cashWithdrawalRequestValidateResult)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }
}