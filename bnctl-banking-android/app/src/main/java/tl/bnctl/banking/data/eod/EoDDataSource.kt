package tl.bnctl.banking.data.eod

import com.google.gson.Gson
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class EoDDataSource (
    private val eodService: EoDService
) {

    val gson: Gson = Gson()

    suspend fun eodCheck(): Result<Boolean> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            /*val eodCheckResult = eodService.checkEoD(accessToken)
            return Result.Success(gson.fromJson(eodCheckResult.get("result").toString(), Boolean::class.java))*/
            return Result.Success(false);
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

}