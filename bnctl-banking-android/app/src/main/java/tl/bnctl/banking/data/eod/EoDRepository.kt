package tl.bnctl.banking.data.eod

import tl.bnctl.banking.data.Result

class EoDRepository(private val dataSource: EoDDataSource) {

    var isEoD: Boolean = false

    suspend fun checkEoD(): Result<Boolean> {
        val result = dataSource.eodCheck()
        if (result is Result.Success) {
            isEoD = result.data
        }
        return result
    }


}