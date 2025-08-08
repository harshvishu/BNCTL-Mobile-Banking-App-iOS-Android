package tl.bnctl.banking.data.billers

import com.google.gson.Gson
import com.google.gson.JsonArray
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.billers.model.UtilityBill
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class BillerDataSource(private val billerService: BillerService) {

    val gson: Gson = Gson()

    suspend fun fetchMyBillers(): Result<List<UtilityBill>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val myBillersResult: JsonArray
            billerService.myBillers(accessToken).also { myBillersResult = it }
            val myBillersList: List<UtilityBill> = myBillersResult.map { myBillerObject ->
                gson.fromJson(
                    myBillerObject.toString(),
                    UtilityBill::class.java
                )
            }
            return Result.Success(myBillersList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (e: Exception) {
            return Result.createError(e)
        }
    }
}