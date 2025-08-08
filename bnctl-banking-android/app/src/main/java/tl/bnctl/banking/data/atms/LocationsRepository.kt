package tl.bnctl.banking.data.atms

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.atms.model.LocationObj

class LocationsRepository(private val dataSource: LocationsDataSource) {

    private var locations: List<LocationObj>? = null

    suspend fun atmsFetch(): Result<List<LocationObj>> {
        val result = dataSource.fetchAtms()
        if (result is Result.Success) {
            locations = result.data
        }
        return result
    }

    suspend fun branchesFetch(): Result<List<LocationObj>> {
        val result = dataSource.fetchBranches()
        if (result is Result.Success) {
            locations = result.data
        }
        return result
    }
}