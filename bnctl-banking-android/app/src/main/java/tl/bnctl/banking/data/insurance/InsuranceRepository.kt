package tl.bnctl.banking.data.insurance

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.insurance.model.Insurance

class InsuranceRepository(private val dataSource: InsuranceDataSource) {

    var insurances: List<Insurance>? = null

    suspend fun fetchInsurances(): Result<List<Insurance>> {
        val result = dataSource.fetchInsurances()
        if (result is Result.Success) {
            insurances = result.data
        }
        return result
    }
}
