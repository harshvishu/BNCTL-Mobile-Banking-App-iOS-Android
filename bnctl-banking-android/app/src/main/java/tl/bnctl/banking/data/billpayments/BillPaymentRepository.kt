package tl.bnctl.banking.data.billpayments

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.billpayments.model.BillPaymentHistory
import java.util.*

class BillPaymentRepository(private val dataSource: BillPaymentDataSource) {

    var billPaymentHistory: List<BillPaymentHistory>? = null
        private set

    suspend fun fetchBillPayments(fromDate: Date, toDate: Date): Result<List<BillPaymentHistory>> {
        val result = dataSource.fetchBillPayments(fromDate, toDate)
        if (result is Result.Success) {
            billPaymentHistory = result.data
        }
        return result
    }
}