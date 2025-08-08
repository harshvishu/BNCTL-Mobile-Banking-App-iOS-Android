package tl.bnctl.banking.data.billers

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.billers.model.UtilityBill

class BillerRepository(private val dataSource: BillerDataSource) {

    var myBillers: List<UtilityBill>? = null
        private set

    suspend fun fetchMyBillers(): Result<List<UtilityBill>> {
        val result = dataSource.fetchMyBillers()
        if (result is Result.Success) {
            myBillers = result.data
        }
        return result
    }
}