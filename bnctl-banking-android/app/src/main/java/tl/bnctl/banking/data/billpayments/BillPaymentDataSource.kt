package tl.bnctl.banking.data.billpayments

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.billpayments.model.BillPaymentHistory
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.ui.utils.DateUtils
import tl.bnctl.banking.util.Constants
import java.util.*

class BillPaymentDataSource(private val billPaymentService: BillPaymentService) {

    val gson: Gson = Gson()

    suspend fun fetchBillPayments(fromDate: Date, toDate: Date): Result<List<BillPaymentHistory>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error(
                    "Session expired",
                    Constants.SESSION_EXPIRED_CODE
                )
            val billPaymentResult: JsonObject
            billPaymentService.fetchBillPayments(
                accessToken, DateUtils.formatDateISO(fromDate), DateUtils.formatDateISO(toDate)
            ).also { billPaymentResult = it }
            val billPaymentList: List<BillPaymentHistory> =
                billPaymentResult.getAsJsonArray("records").map { billPaymentJsonObject ->
                    gson.fromJson(billPaymentJsonObject.toString(), BillPaymentHistory::class.java)
                }
            return Result.Success(billPaymentList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (e: Exception) {
            return Result.createError(e)
        }
    }

}