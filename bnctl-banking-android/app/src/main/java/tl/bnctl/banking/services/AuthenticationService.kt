package tl.bnctl.banking.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import tl.bnctl.banking.data.login.model.LoggedInUser
import tl.bnctl.banking.data.login.model.TokenData

class AuthenticationService private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            tl.bnctl.banking.BankingApplication.appCode,
            Context.MODE_PRIVATE
        )

    companion object {
        @Volatile
        private var INSTANCE: AuthenticationService? = null

        fun initAuthenticationService(context: Context): AuthenticationService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthenticationService(context).also { INSTANCE = it }
            }

        fun getInstance(): AuthenticationService {
            return INSTANCE!!
        }
    }

    fun saveAuthenticationData(loggedInUser: LoggedInUser) {
        // TODO: Replace with Room
        with(sharedPreferences.edit()) {
            putString("username", loggedInUser.authData.username)
            putString("userId", loggedInUser.authData.userId)
            putString("accessToken", loggedInUser.tokenData.accessToken)
            putString("refreshToken", loggedInUser.tokenData.refreshToken)
            putBoolean("hasLoggedInBefore", true)
            putString("permissions", Gson().toJson(loggedInUser.authData.permissions))
            apply()
        }
    }

    fun editCustomerNumberInAuthenticationData(customerNumber: String) {
        val accessToken = sharedPreferences.getString("accessToken", "")!!
        val accessTokenParts = accessToken.split(".")
        with(sharedPreferences.edit()) {
            putString(
                "accessToken",
                "${accessTokenParts[0]}.${customerNumber}.${accessTokenParts[2]}"
            )
            apply()
        }
    }

    fun getAuthToken(): String? {
        val token = sharedPreferences.getString("accessToken", "") ?: return null
        return "Bearer $token"
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refreshToken", "")
    }

    fun getTokenData(): TokenData {
        val token = sharedPreferences.getString("accessToken", "")
        val refreshToken = sharedPreferences.getString("refreshToken", "")
        val expiresIn = sharedPreferences.getInt("expiresIn", 0)
        val username = sharedPreferences.getString("username", "")
        val userId = sharedPreferences.getString("userId", "")
        return TokenData(token, refreshToken, expiresIn, null, username, userId)
    }

    fun logout() {
        with(sharedPreferences.edit()) {
            remove("accessToken")
            apply()
        }
    }

    fun saveRefreshedToken(accessToken: String?, refreshToken: String?) {
        with(sharedPreferences.edit()) {
            putString("accessToken", accessToken)
            putString("refreshToken", refreshToken)
            apply()
        }
    }
}