package tl.bnctl.banking.data.branches

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.branches.model.Branch

class BranchDataSource(private val branchService: BranchService) {

    val gson: Gson = Gson()

    suspend fun fetchBranches(): Result<List<Branch>> {
        Log.v(TAG, "Getting branches")
        return try {
            val branchesResult: JsonObject = branchService.fetchBranches()
            val branchesList: List<Branch> =
                (branchesResult.get("result") as JsonArray).map { branchJsonObject ->
                    gson.fromJson(branchJsonObject.toString(), Branch::class.java)
                }
            Log.v(TAG, "Returned ${branchesList.size} branches")
            Result.Success(branchesList)
        } catch (retrofitEx: HttpException) {
            Result.createError(retrofitEx)
        } catch (ex: Exception) {
            Result.createError(ex)
        }
    }

    companion object {
        const val TAG = "BranchesDataSource"
    }
}