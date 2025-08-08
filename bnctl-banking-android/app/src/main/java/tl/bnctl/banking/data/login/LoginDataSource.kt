package tl.bnctl.banking.data.login

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.fallback.SendFallbackSMSResult
import tl.bnctl.banking.data.login.model.*
import tl.bnctl.banking.data.login.model.accsessPolicy.AccessPolicy
import tl.bnctl.banking.data.login.model.accsessPolicy.ValidationPolicy
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class LoginDataSource(private val loginService: LoginService) {

    val gson: Gson = Gson()

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // Prepare and send request
            val loginResult = loginService.login(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            val loggedInUser = parseLoginResult(loginResult)
            Firebase.crashlytics.setUserId(username)
            return Result.Success(loggedInUser)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    private fun parseLoginResult(loginResult: JsonObject): LoggedInUser {
        // Parse returned result
        val resultJsonObject = loginResult.get("result").asJsonObject
        val authData =
            gson.fromJson(resultJsonObject.get("auth").toString(), AuthData::class.java)
        authData.permissions = parsePermissions(resultJsonObject)
        val tokenData =
            gson.fromJson(resultJsonObject.toString(), TokenData::class.java)
        // TODO: Make backend return customer data.
        val firstName = "Nicholas"
        val lastName = "The Client"
        val customer = CustomerData(firstName, lastName, "individual")

        // Create the returned object
        val loggedInUser = LoggedInUser(customer, tokenData, authData)
        return loggedInUser
    }

    private fun parsePermissions(resultJsonObject: JsonObject): List<Permission> {
        val user = (resultJsonObject.get("user") as JsonObject)
        val userRoles = user.get("userRoles") as JsonArray
        val permissions = ArrayList<Permission>()
        if (userRoles.isEmpty) {
            return permissions
        } else {
            val role = userRoles.get(0) as JsonObject
            val attributes = role.get("attributes") as JsonObject
            val keySetAttributes = attributes.keySet()
            if (keySetAttributes.isEmpty()) {
                return permissions
            } else {
                keySetAttributes.forEach { key ->
                    permissions.add(Permission(key, attributes.get(key).asString))
                }
            }
        }
        return permissions
    }

    suspend fun sendFallbackSMS(
        username: String,
        password: String
    ): Result<SendFallbackSMSResult> {
        try {
            val sendSMSData = loginService.sendFallbackSMS(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            val authData =
                gson.fromJson(
                    sendSMSData.get("result").toString(),
                    SendFallbackSMSResult::class.java
                )
            return Result.Success(authData)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    suspend fun confirmFallback(
        username: String,
        password: String,
        smsCode: String,
        pin: String? = null
    ): Result<LoggedInUser> {
        try {
            val body: MutableMap<String, String> = mutableMapOf(
                "username" to username,
                "password" to password,
                "smsCode" to smsCode
            )
            if (pin != null) {
                body["pin"] = pin
            }
            val loginResult = loginService.confirmFallback(
                body
            )
            val loggedInUser = parseLoginResult(loginResult)

            return Result.Success(loggedInUser)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx, "login")
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    suspend fun logout() {
        val accessToken = AuthenticationService.getInstance().getAuthToken()
        try {
            accessToken?.let { loginService.logout(it) }
        } finally {
            AuthenticationService.getInstance().logout()
        }
    }

    suspend fun changePassword(
        username: String,
        oldPassword: String,
        newPassword: String
    ): Result<Boolean> {
        val accessToken = AuthenticationService.getInstance().getAuthToken() ?: return Result.Error(
            "Session expired",
            Constants.SESSION_EXPIRED_CODE,
            "sessionExpired"
        )

        return try {
            val changePasswordResponse = loginService.changePassword(
                accessToken,
                mapOf(
                    "username" to username,
                    "password" to oldPassword,
                    "newPassword" to newPassword
                )
            )
            Result.Success(parsePasswordChangeResult(changePasswordResponse))
        } catch (retrofitEx: HttpException) {
            Result.createError(retrofitEx)
        } catch (ex: Exception) {
            Result.createError(ex)
        }
    }

    suspend fun changeUsername(oldUsername: String, newUsername: String): Result<Boolean>? {
        val accessToken = AuthenticationService.getInstance().getAuthToken() ?: return Result.Error(
            "Session expired",
            Constants.SESSION_EXPIRED_CODE,
            "sessionExpired"
        )

        return try {
            val changeUsernameResponse = loginService.changeUsername(
                accessToken,
                mapOf(
                    "oldUsername" to oldUsername,
                    "newUsername" to newUsername
                )
            )
            Result.Success(parseUsernameChangeResult(changeUsernameResponse))
        } catch (retrofitEx: HttpException) {
            Result.createError(retrofitEx)
        } catch (ex: Exception) {
            Result.createError(ex)
        }
    }

    suspend fun fetchAccessPolicy(): Result<AccessPolicy> {
        val accessToken = AuthenticationService.getInstance().getAuthToken() ?: return Result.Error(
            "Session expired",
            Constants.SESSION_EXPIRED_CODE,
            "sessionExpired"
        )

        return try {
            val policyResponse = loginService.fetchAccessPolicy(accessToken)
            Result.Success(parseAccessPolicyResponse(policyResponse))
        } catch (retrofitEx: HttpException) {
            Result.createError(retrofitEx)
        } catch (ex: Exception) {
            Result.createError(ex)
        }
    }

    private fun parseAccessPolicyResponse(policyResponse: JsonObject): AccessPolicy {
        val resultJsonObject = policyResponse.getAsJsonObject("result")
        val usernamePolicy = gson.fromJson(
            resultJsonObject.get("usernamePolicy").asJsonArray.get(0).toString(),
            ValidationPolicy::class.java
        )
        val passwordPolicy = gson.fromJson(
            resultJsonObject.get("passwordPolicy").asJsonArray.get(0).toString(),
            ValidationPolicy::class.java
        )
        return AccessPolicy(usernamePolicy, passwordPolicy)
    }

    private fun parsePasswordChangeResult(changePasswordResponse: JsonObject): Boolean {
        // Parse returned result
        val resultJsonObject = changePasswordResponse.get("result").asJsonObject
        return resultJsonObject.get("attributes").asJsonObject.get("passwordChanged").asBoolean
    }

    private fun parseUsernameChangeResult(changeUsernameResponse: JsonObject): Boolean {
        // Parse returned result
        val resultJsonObject = changeUsernameResponse.get("result").asJsonObject
        return resultJsonObject.get("attributes").asJsonObject.get("usernameChanged").asBoolean
    }
}