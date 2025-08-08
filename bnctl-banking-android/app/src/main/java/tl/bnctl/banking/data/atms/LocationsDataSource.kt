package tl.bnctl.banking.data.atms

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.atms.model.LocationObj

class LocationsDataSource(
    private val locationsService: LocationsService
) {
    val gson: Gson = Gson()

    suspend fun fetchAtms(): Result<List<LocationObj>> {
        return fetchLocations("atms")
    }

    suspend fun fetchBranches(): Result<List<LocationObj>> {
        return fetchLocations("branches")
    }


    private suspend fun fetchLocations(fetchType: String): Result<List<LocationObj>> {
        return try {
            var jsonResultLocations = JsonArray()
            when (fetchType) {
                "atms" -> locationsService.fetchAtms()
                    .also { jsonResultLocations = it.get("records") as JsonArray }
                "branches" -> locationsService.fetchBranches()
                    .also { jsonResultLocations = it.get("records") as JsonArray }
            }

            val atmsList = ArrayList<LocationObj>()
            for (atmsJsonObject in jsonResultLocations) {
                transformJsonToLocationObj(atmsJsonObject.asJsonObject, atmsList)
            }

            Result.Success(atmsList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (e: Exception) {
            return Result.createError(e);
        }
    }

    private fun transformJsonToLocationObj(
        atmJsonObject: JsonObject,
        atmList: ArrayList<LocationObj>
    ) {
        atmList.add(
            LocationObj(
                atmJsonObject.get("id").asString,
                atmJsonObject.get("latitude").asDouble,
                atmJsonObject.get("longitude").asDouble,
                atmJsonObject.get("name").asString,
                atmJsonObject.get("name").asString,
                atmJsonObject.get("address").asString
            )
        )
    }
}