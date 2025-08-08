package tl.bnctl.banking.data.current_user

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.login.model.*
import tl.bnctl.banking.services.AuthenticationService

class CurrentUserDataSource(private val currentUserService: CurrentUserService) {

    val gson: Gson = Gson()

    suspend fun getCurrentUser(): Result<LoggedInUser>? {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken() ?: return null
            val currentUserResult: JsonObject
            currentUserService.currentUser(accessToken).also { currentUserResult = it }

            val resultJsonObject = currentUserResult.get("result").asJsonObject

            val authData =
                gson.fromJson(resultJsonObject.get("auth").toString(), AuthData::class.java)
            authData.permissions = parsePermissions(resultJsonObject)
            val customer =
                gson.fromJson(resultJsonObject.get("person").toString(), CustomerData::class.java)

            val tokenData =
                TokenData(
                    AuthenticationService.getInstance().getAuthToken(),
                    AuthenticationService.getInstance().getRefreshToken(),
                    0, 0,
                    authData.username, authData.userId
                )
            val loggedInUser =
                LoggedInUser(
                    customer,
                    tokenData,
                    authData
                )
            AuthenticationService.getInstance().saveAuthenticationData(loggedInUser)
            return Result.Success(loggedInUser)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    private fun parsePermissions(resultJsonObject: JsonObject): List<Permission> {
        val auth = (resultJsonObject.get("auth") as JsonObject)
        val permissions = auth.get("permissions") as JsonArray
        val parsedPermissions = ArrayList<Permission>()
        if (permissions.isEmpty) {
            return parsedPermissions
        } else {
            permissions.forEach { permission ->
                parsedPermissions.add(gson.fromJson(permission, Permission::class.java))
            }
        }
        return parsedPermissions
    }
}