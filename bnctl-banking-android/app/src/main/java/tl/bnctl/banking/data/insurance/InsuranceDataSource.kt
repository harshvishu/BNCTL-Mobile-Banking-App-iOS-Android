package tl.bnctl.banking.data.insurance

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.insurance.model.Insurance
import tl.bnctl.banking.services.AuthenticationService

class InsuranceDataSource(private val insuranceService: InsuranceService) {

    val gson: Gson = Gson()

    suspend fun fetchInsurances(): Result<List<Insurance>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error(
                    "Session expired",
                    tl.bnctl.banking.util.Constants.SESSION_EXPIRED_CODE
                )
            val insurancesResult: JsonObject
            insuranceService.fetchInsurances(accessToken).also { insurancesResult = it }
            val insurancesJsonArray: JsonArray = insurancesResult.get("result") as JsonArray
            val insurancesList: List<Insurance> = insurancesJsonArray.map { insuranceJsonObject ->
                gson.fromJson(insuranceJsonObject.toString(), Insurance::class.java)
            }
            return Result.Success(insurancesList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

}
